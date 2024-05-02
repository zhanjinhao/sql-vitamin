package cn.addenda.sql.vitamin.rewriter.baseentity;

import cn.addenda.sql.vitamin.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamin.rewriter.visitor.item.*;
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
public class DruidBaseEntityRewriter implements BaseEntityRewriter {

  private final BaseEntitySource baseEntitySource;

  /**
   * 需要基础字段的表
   */
  private final List<String> included;

  /**
   * 不需要基础字段的表
   */
  private final List<String> excluded;

  private final DataConvertorRegistry dataConvertorRegistry;

  protected static final List<String> INSERT_COLUMN_NAME_LIST;
  protected static final List<String> INSERT_FIELD_NAME_LIST;
  protected static final List<String> UPDATE_COLUMN_NAME_LIST;
  protected static final List<String> UPDATE_FIELD_NAME_LIST;

  static {
    INSERT_COLUMN_NAME_LIST = BaseEntity.getAllColumnNameList();
    INSERT_FIELD_NAME_LIST = BaseEntity.getAllFieldNameList();
    UPDATE_COLUMN_NAME_LIST = BaseEntity.getUpdateColumnNameList();
    UPDATE_FIELD_NAME_LIST = BaseEntity.getUpdateFieldNameList();
  }

  public DruidBaseEntityRewriter(
      List<String> included, List<String> excluded,
      BaseEntitySource baseEntitySource, DataConvertorRegistry dataConvertorRegistry) {
    this.baseEntitySource = baseEntitySource;
    this.included = included;
    this.excluded = excluded;
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

  public DruidBaseEntityRewriter() {
    this(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
  }

  @Override
  public String rewriteInsertSql(
      String sql, InsertAddSelectItemMode insertAddSelectItemMode,
      boolean duplicateKeyUpdate, UpdateItemMode updateItemMode) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {

      for (int i = 0; i < INSERT_COLUMN_NAME_LIST.size(); i++) {
        String columnName = INSERT_COLUMN_NAME_LIST.get(i);
        String fieldName = INSERT_FIELD_NAME_LIST.get(i);
        Item item = new Item(columnName, baseEntitySource.get(fieldName));
        new AddInsertItemVisitor((MySqlInsertStatement) sqlStatement, included, excluded,
            dataConvertorRegistry, true, item, insertAddSelectItemMode,
            duplicateKeyUpdate && UPDATE_FIELD_NAME_LIST.contains(fieldName), updateItemMode).visit();
      }

      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String rewriteSelectSql(String sql, String masterView) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      for (String column : INSERT_COLUMN_NAME_LIST) {
        new AddSelectItemVisitor((SQLSelectStatement) sqlStatement,
            included, excluded, false, masterView, column).visit();
      }
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String rewriteUpdateSql(String sql, UpdateItemMode updateItemMode) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      for (int i = 0; i < UPDATE_COLUMN_NAME_LIST.size(); i++) {
        String columnName = UPDATE_COLUMN_NAME_LIST.get(i);
        String fieldName = UPDATE_FIELD_NAME_LIST.get(i);
        Item item = new Item(columnName, baseEntitySource.get(fieldName));
        new AddUpdateItemVisitor((MySqlUpdateStatement) sqlStatement,
            included, excluded, dataConvertorRegistry, true, item, updateItemMode).visit();
      }

      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

}
