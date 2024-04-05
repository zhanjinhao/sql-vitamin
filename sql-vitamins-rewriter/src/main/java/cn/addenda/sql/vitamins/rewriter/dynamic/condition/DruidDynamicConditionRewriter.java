package cn.addenda.sql.vitamins.rewriter.dynamic.condition;

import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.TableAddJoinConditionVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.TableAddWhereConditionVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.ViewAddJoinConditionVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.condition.ViewAddWhereConditionVisitor;

import java.util.List;

/**
 * @author addenda
 * @since 2023/4/30 16:56
 */
public class DruidDynamicConditionRewriter implements DynamicConditionRewriter {

  // todo excluded
  private final List<String> notIncluded;

  public DruidDynamicConditionRewriter() {
    this.notIncluded = null;
  }

  public DruidDynamicConditionRewriter(List<String> notIncluded) {
    this.notIncluded = notIncluded;
  }

  @Override
  public String tableAddJoinCondition(
      String sql, String tableOrViewName, String condition, boolean useSubQuery) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      sqlStatement.accept(new TableAddJoinConditionVisitor(
          tableOrViewName == null ? null : ArrayUtils.asArrayList(tableOrViewName), notIncluded,
          condition, useSubQuery, true));
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String viewAddJoinCondition(
      String sql, String tableOrViewName, String condition, boolean useSubQuery) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      sqlStatement.accept(new ViewAddJoinConditionVisitor(
          tableOrViewName == null ? null : ArrayUtils.asArrayList(tableOrViewName), notIncluded,
          condition, useSubQuery, true));
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String tableAddWhereCondition(String sql, String tableOrViewName, String condition) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      sqlStatement.accept(new TableAddWhereConditionVisitor(
          tableOrViewName == null ? null : ArrayUtils.asArrayList(tableOrViewName), notIncluded, condition));
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

  @Override
  public String viewAddWhereCondition(String sql, String tableOrViewName, String condition) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      sqlStatement.accept(new ViewAddWhereConditionVisitor(
          tableOrViewName == null ? null : ArrayUtils.asArrayList(tableOrViewName), notIncluded, condition));
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

}
