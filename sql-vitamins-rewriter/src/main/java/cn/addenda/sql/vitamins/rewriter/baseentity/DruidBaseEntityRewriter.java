package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.item.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author addenda
 * @since 2023/5/2 19:35
 */
@Slf4j
public class DruidBaseEntityRewriter extends AbstractBaseEntityRewriter {

  public DruidBaseEntityRewriter(
      List<String> included, List<String> notIncluded,
      BaseEntitySource baseEntitySource, DataConvertorRegistry dataConvertorRegistry) {
    super(baseEntitySource, included, notIncluded, dataConvertorRegistry);
  }

  public DruidBaseEntityRewriter() {
    this(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
  }

  @Override
  public String rewriteInsertSql(
      String sql, InsertAddSelectItemMode insertAddSelectItemMode,
      boolean duplicateKeyUpdate, UpdateItemMode updateItemMode, boolean reportItemNameExists) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {

      for (int i = 0; i < INSERT_COLUMN_NAME_LIST.size(); i++) {
        String columnName = INSERT_COLUMN_NAME_LIST.get(i);
        String fieldName = INSERT_FIELD_NAME_LIST.get(i);
        Item item = new Item(columnName, baseEntitySource.get(fieldName));
        new AddInsertItemVisitor((MySqlInsertStatement) sqlStatement, included, notIncluded,
            // todo 考虑在将reportItemNameExists写死为true。在控制层提供忽略字段的注解
            dataConvertorRegistry, reportItemNameExists, item, insertAddSelectItemMode,
            duplicateKeyUpdate && UPDATE_FIELD_NAME_LIST.contains(fieldName), updateItemMode).visit();
      }

      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String rewriteSelectSql(String sql, String masterView) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      for (String column : INSERT_COLUMN_NAME_LIST) {
        new AddSelectItemVisitor((SQLSelectStatement) sqlStatement, included, notIncluded,
            false, masterView, column).visit();
      }
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String rewriteUpdateSql(
      String sql, UpdateItemMode updateItemMode, boolean reportItemNameExists) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      for (int i = 0; i < UPDATE_COLUMN_NAME_LIST.size(); i++) {
        String columnName = UPDATE_COLUMN_NAME_LIST.get(i);
        String fieldName = UPDATE_FIELD_NAME_LIST.get(i);
        Item item = new Item(columnName, baseEntitySource.get(fieldName));
        new AddUpdateItemVisitor((MySqlUpdateStatement) sqlStatement, included,
            // todo 考虑在将reportItemNameExists写死为true。在控制层提供忽略字段的注解
            notIncluded, dataConvertorRegistry, reportItemNameExists, item, updateItemMode).visit();
      }

      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

}
