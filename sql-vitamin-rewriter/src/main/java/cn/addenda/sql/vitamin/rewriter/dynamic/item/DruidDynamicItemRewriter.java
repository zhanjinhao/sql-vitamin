package cn.addenda.sql.vitamin.rewriter.dynamic.item;

import cn.addenda.sql.vitamin.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamin.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamin.rewriter.visitor.item.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;

import java.util.List;

/**
 * @author addenda
 * @since 2023/4/30 16:56
 */
public class DruidDynamicItemRewriter implements DynamicItemRewriter {
  private final List<String> excluded;

  private final DataConvertorRegistry dataConvertorRegistry;

  public DruidDynamicItemRewriter(DataConvertorRegistry dataConvertorRegistry) {
    this.excluded = null;
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

  public DruidDynamicItemRewriter(List<String> excluded, DataConvertorRegistry dataConvertorRegistry) {
    this.excluded = excluded;
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

  @Override
  public String insertAddItem(
          String sql, String tableName, Item item, InsertAddSelectItemMode insertAddSelectItemMode,
          boolean duplicateKeyUpdate, UpdateItemMode updateItemMode) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      MySqlInsertStatement insertStatement = (MySqlInsertStatement) sqlStatement;
      new AddInsertItemVisitor(
          insertStatement, tableName == null ? null : ArrayUtils.asArrayList(tableName), excluded,
          dataConvertorRegistry, true, item,
          insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode).visit();
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String updateAddItem(
      String sql, String tableName, Item item, UpdateItemMode updateItemMode) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      MySqlUpdateStatement updateStatement = (MySqlUpdateStatement) sqlStatement;

      new AddUpdateItemVisitor(
          updateStatement, tableName == null ? null : ArrayUtils.asArrayList(tableName), excluded,
          dataConvertorRegistry, true, item, updateItemMode).visit();
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

}
