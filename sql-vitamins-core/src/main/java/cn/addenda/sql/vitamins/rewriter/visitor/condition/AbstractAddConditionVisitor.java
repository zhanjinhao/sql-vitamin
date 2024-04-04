package cn.addenda.sql.vitamins.rewriter.visitor.condition;

import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2023/5/11 16:27
 */
@Slf4j
public abstract class AbstractAddConditionVisitor extends MySqlASTVisitorAdapter {

  protected final List<String> included;
  protected final List<String> notIncluded;

  protected final String condition;

  protected static final String TABLE_NAME_KEY = "tableNameKey";
  protected static final String ALIAS_KEY = "aliasKey";

  protected AbstractAddConditionVisitor(List<String> included, List<String> notIncluded, String condition) {
    this.included = included;
    this.notIncluded = notIncluded;
    this.condition = condition;
  }

  @Override
  public void endVisit(SQLExprTableSource x) {
    String aAlias = x.getAlias();
    String aTableName = x.getTableName();
    if (!JdbcSQLUtils.include(determineTableName(aTableName, aAlias), included, notIncluded)) {
      return;
    }
    addTableName(x, aTableName);
    addAlias(x, aAlias);
  }

  @Override
  public void endVisit(MySqlDeleteStatement x) {
    SQLTableSource tableSource = x.getTableSource();
    // delete 语句只会操作一张表
    String aTableName = getTableName(tableSource);
    String aAlias = getAlias(tableSource);
    if (aTableName != null) {
      // where 语句只能使用where条件，不能使用sub query或join
      SQLExpr where = newWhere(x.getWhere(), aTableName, aAlias);
      logWhereChange(x, x.getWhere(), where);
      x.setWhere(where);
      clear(tableSource);
    }
  }

  protected SQLTableSource newFrom(String tableName, String alias) {
    String view = alias == null ? tableName : alias;
    SQLSelectStatement sqlStatement = (SQLSelectStatement) SQLUtils.parseSingleStatement(
      "select * from (select * from " + tableName + " where " + condition + ")", DbType.mysql);
    SQLTableSource from = ((SQLSelectQueryBlock) sqlStatement.getSelect().getQuery()).getFrom();
    from.setAlias(view);
    return from;
  }

  protected SQLExpr newWhere(SQLExpr where, String tableName, String alias) {
    String view = alias == null ? tableName : alias;
    return SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd,
      SQLUtils.toSQLExpr(view + "." + condition, DbType.mysql), false, where);
  }

  protected String getTableName(SQLObject sqlObject) {
    List<String> tableNameList = getTableNameList(sqlObject);
    if (tableNameList == null) {
      return null;
    }
    return tableNameList.get(0);
  }

  protected String getAlias(SQLObject sqlObject) {
    List<String> aliasList = getAliasList(sqlObject);
    if (aliasList == null) {
      return null;
    }
    return aliasList.get(0);
  }

  protected List<String> getTableNameList(SQLObject sqlObject) {
    return (List<String>) sqlObject.getAttribute(TABLE_NAME_KEY);
  }

  protected List<String> getAliasList(SQLObject sqlObject) {
    return (List<String>) sqlObject.getAttribute(ALIAS_KEY);
  }

  protected void addTableName(SQLObject sqlObject, String tableName) {
    List<String> attribute = (List<String>) sqlObject.getAttribute(TABLE_NAME_KEY);
    if (attribute == null) {
      attribute = new ArrayList<>();
      sqlObject.putAttribute(TABLE_NAME_KEY, attribute);
    }
    attribute.add(tableName);
  }

  protected void addAlias(SQLObject sqlObject, String alias) {
    List<String> attribute = (List<String>) sqlObject.getAttribute(ALIAS_KEY);
    if (attribute == null) {
      attribute = new ArrayList<>();
      sqlObject.putAttribute(ALIAS_KEY, attribute);
    }
    attribute.add(alias);
  }

  protected void clear(SQLObject sqlObject) {
    sqlObject.getAttributes().remove(TABLE_NAME_KEY);
    sqlObject.getAttributes().remove(ALIAS_KEY);
  }

  protected String determineTableName(String tableName, String alias) {
    return tableName;
  }


  protected void logWhereChange(SQLObject x, SQLObject before, SQLObject after) {
    if (log.isDebugEnabled()) {
      log.debug("SQLObject: [{}]，where before：[{}]，where after：[{}]。",
        DruidSQLUtils.toLowerCaseSQL(x),
        DruidSQLUtils.toLowerCaseSQL(before),
        DruidSQLUtils.toLowerCaseSQL(after));
    }
  }

  protected void logTableSourceChange(SQLObject x, SQLObject before, SQLObject after) {
    if (log.isDebugEnabled()) {
      log.debug("SQLObject: [{}]. TableSource before: [{}]; TableSource after: [{}].",
        DruidSQLUtils.toLowerCaseSQL(x),
        DruidSQLUtils.toLowerCaseSQL(before),
        DruidSQLUtils.toLowerCaseSQL(after));
    }
  }

  protected void logJoinConditionChange(SQLObject x, SQLObject before, SQLObject after) {
    if (log.isDebugEnabled()) {
      log.debug("SQLObject: [{}]. JoinCondition before: [{}]; JoinCondition after: [{}].",
        DruidSQLUtils.toLowerCaseSQL(x),
        DruidSQLUtils.toLowerCaseSQL(before),
        DruidSQLUtils.toLowerCaseSQL(after));
    }
  }

  @Override
  public String toString() {
    return "AbstractAddConditionVisitor{" +
      "included=" + included +
      ", notIncluded=" + notIncluded +
      ", condition='" + condition + '\'' +
      "} " + super.toString();
  }
}
