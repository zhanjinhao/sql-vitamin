package cn.addenda.sql.vitamins.rewriter.dynamicsql;

import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.TableAddJoinConditionVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.TableAddWhereConditionVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.ViewAddJoinConditionVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.ViewAddWhereConditionVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.item.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;

/**
 * @author addenda
 * @since 2023/4/30 16:56
 */
public class DruidDynamicRewriter implements DynamicRewriter {

  private final DataConvertorRegistry dataConvertorRegistry;

  public DruidDynamicRewriter(DataConvertorRegistry dataConvertorRegistry) {
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

  @Override
  public String tableAddJoinCondition(
      String sql, String tableName, String condition, boolean useSubQuery) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      sqlStatement.accept(new TableAddJoinConditionVisitor(tableName, condition, useSubQuery));
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String viewAddJoinCondition(
      String sql, String tableName, String condition, boolean useSubQuery) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      sqlStatement.accept(new ViewAddJoinConditionVisitor(tableName, condition, useSubQuery));
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String tableAddWhereCondition(String sql, String tableName, String condition) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      sqlStatement.accept(new TableAddWhereConditionVisitor(tableName, condition));
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String viewAddWhereCondition(String sql, String tableName, String condition) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      sqlStatement.accept(new ViewAddWhereConditionVisitor(tableName, condition));
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String insertAddItem(String sql, String tableName, Item item, InsertAddSelectItemMode insertAddSelectItemMode,
                              boolean duplicateKeyUpdate, UpdateItemMode updateItemMode) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      MySqlInsertStatement insertStatement = (MySqlInsertStatement) sqlStatement;
      new AddInsertItemVisitor(
          insertStatement, tableName == null ? null : ArrayUtils.asArrayList(tableName), null,
          dataConvertorRegistry, true, item,
          insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode).visit();
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String updateAddItem(String sql, String tableName, Item item, UpdateItemMode updateItemMode) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      MySqlUpdateStatement updateStatement = (MySqlUpdateStatement) sqlStatement;

      new AddUpdateItemVisitor(
          updateStatement, tableName == null ? null : ArrayUtils.asArrayList(tableName), null,
          dataConvertorRegistry, true, item, updateItemMode).visit();
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

}
