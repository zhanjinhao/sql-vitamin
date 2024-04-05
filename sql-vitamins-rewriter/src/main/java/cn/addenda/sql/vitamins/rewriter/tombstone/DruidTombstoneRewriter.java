package cn.addenda.sql.vitamins.rewriter.tombstone;

import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.TableAddJoinConditionVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.identifier.IdentifierExistsVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.item.AddInsertItemVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.Item;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author addenda
 * @since 2023/4/30 19:42
 */
@Slf4j
public class DruidTombstoneRewriter implements TombstoneRewriter {

  private static final String TOMBSTONE_NAME = "if_del";
  private static final Integer NON_TOMBSTONE_VALUE = 0;
  private static final Integer TOMBSTONE_VALUE = 1;
  private static final String NON_TOMBSTONE = TOMBSTONE_NAME + "=" + NON_TOMBSTONE_VALUE;
  private static final String TOMBSTONE = TOMBSTONE_NAME + "=" + TOMBSTONE_VALUE;
  private static final String DELETE_TIME_NAME = "delete_time";
  private static final String DELETE_TIME_VALUE = "now(3)";
  private static final String DELETE_TIME = DELETE_TIME_NAME + "=" + DELETE_TIME_VALUE;
  private static final Item TOMBSTONE_ITEM = new Item(TOMBSTONE_NAME, NON_TOMBSTONE_VALUE);

  /**
   * 逻辑删除的表
   */
  private final List<String> included;

  /**
   * 非逻辑删除的表
   */
  private final List<String> excluded;

  private final DataConvertorRegistry dataConvertorRegistry;

  public DruidTombstoneRewriter(List<String> included, List<String> excluded, DataConvertorRegistry dataConvertorRegistry) {
    this.included = included;
    this.excluded = excluded;
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

  public DruidTombstoneRewriter() {
    this(null, null, new DefaultDataConvertorRegistry());
  }

  public String rewriteInsertSql(String sql, boolean useSubQuery) {
    return DruidSQLUtils.statementMerge(sql, s -> doRewriteInsertSql(s, useSubQuery));
  }

  /**
   * insert语句增加item
   */
  private String doRewriteInsertSql(SQLStatement sqlStatement, boolean useSubQuery) {
    doRewriteSql(sqlStatement, sql -> {
      MySqlInsertStatement insertStatement = (MySqlInsertStatement) sqlStatement;
      // insert into A(..., if_del) values(..., 0)

      // InsertSelectAddItemMode.ITEM：新增的数据if_del都为0，所以用ITEM模式

      // duplicateKeyUpdate为false的原因如下：
      // 在物理删除的场景下，如果冲突，此条数据业务上一定是存在的。
      // 在逻辑删除的场景下，如果冲突，此条数据业务上可能存在也可能不存在
      // - 业务上不存在：执行duplicateKeyUpdate，if_del = 0，会让此条数据存在。与真实业务背离
      // - 业务上存在：执行duplicateKeyUpdate，if_del = 0，不会有任何影响

      //  UpdateItemMode.ALL：固定为0，不可能为空
      new AddInsertItemVisitor(
          insertStatement, included, excluded,
          dataConvertorRegistry, true, TOMBSTONE_ITEM,
          InsertAddSelectItemMode.VALUE, false, UpdateItemMode.ALL).visit();
      // 处理 insert A (...) select ... from B
      sql.accept(new TableAddJoinConditionVisitor(included, excluded, NON_TOMBSTONE, useSubQuery, true));
    });
    return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
  }

  @Override
  public String rewriteDeleteSql(String sql, boolean includeDeleteTime) {
    return DruidSQLUtils.statementMerge(sql,
        sqlStatement -> doRewriteDeleteSql(sqlStatement, includeDeleteTime));
  }

  private String doRewriteDeleteSql(SQLStatement sqlStatement, boolean includeDeleteTime) {
    doRewriteSql(sqlStatement, sql -> {
      // delete from A where ... and if_del = 0
      // false: delete 语句是单表
      sql.accept(new TableAddJoinConditionVisitor(included, excluded, NON_TOMBSTONE, false, true));
    });
    MySqlDeleteStatement mySqlDeleteStatement = (MySqlDeleteStatement) sqlStatement;
    SQLName tableName = mySqlDeleteStatement.getTableName();
    if (!JdbcSQLUtils.include(tableName.toString(), included, excluded)) {
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    }
    SQLExpr where = mySqlDeleteStatement.getWhere();
    // update A set if_del = 1 where ... and if_del = 0
    return "update " + mySqlDeleteStatement.getTableName() + " set " + TOMBSTONE +
        (includeDeleteTime ? " , " + DELETE_TIME : "") +
        " where " + DruidSQLUtils.toLowerCaseSQL(where);
  }

  @Override
  public String rewriteSelectSql(String sql, boolean useSubQuery) {
    return DruidSQLUtils.statementMerge(sql, s -> doRewriteSelectSql(s, useSubQuery));
  }

  /**
   * select增加where条件condition字段
   */
  private String doRewriteSelectSql(SQLStatement sqlStatement, boolean useSubQuery) {
    doRewriteSql(sqlStatement, sql -> {
      // select a from A where ... and if_del = 0
      sql.accept(new TableAddJoinConditionVisitor(included, excluded, NON_TOMBSTONE, useSubQuery, true));
    });
    return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
  }

  @Override
  public String rewriteUpdateSql(String sql) {
    return DruidSQLUtils.statementMerge(sql, this::doRewriteUpdateSql);
  }

  /**
   * update语句where增加condition
   */
  private String doRewriteUpdateSql(SQLStatement sqlStatement) {
    doRewriteSql(sqlStatement, sql -> {
      // update A set ... where ... and if_del = 0
      sql.accept(new TableAddJoinConditionVisitor(included, excluded, NON_TOMBSTONE, false, true));
    });
    return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
  }

  private void doRewriteSql(SQLStatement sqlStatement, Consumer<SQLStatement> consumer) {
    IdentifierExistsVisitor identifierExistsVisitor = new IdentifierExistsVisitor(
        sqlStatement, TOMBSTONE_NAME, included, excluded);
    if (identifierExistsVisitor.isExists()) {
      String msg = String.format("使用逻辑删除的表不能使用[%s]字段，SQL：[%s]。", TOMBSTONE_NAME, DruidSQLUtils.toLowerCaseSQL(sqlStatement));
      throw new TombstoneException(msg);
    }
    consumer.accept(sqlStatement);
  }

}
