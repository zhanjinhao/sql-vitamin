package cn.addenda.sql.vitamins.rewriter.visitor.condition;

import java.util.List;

/**
 * @author addenda
 * @since 2023/4/28 10:05
 */
public class ViewAddJoinConditionVisitor extends TableAddJoinConditionVisitor {

  public ViewAddJoinConditionVisitor(String condition) {
    super(condition);
  }

  public ViewAddJoinConditionVisitor(String tableName, String condition) {
    super(tableName, condition);
  }

  public ViewAddJoinConditionVisitor(String tableName, String condition, boolean useSubQuery) {
    super(tableName, condition, useSubQuery);
  }

  public ViewAddJoinConditionVisitor(List<String> included, List<String> excluded, String condition, boolean useSubQuery, boolean reWriteCommaToJoin) {
    super(included, excluded, condition, useSubQuery, reWriteCommaToJoin);
  }

  @Override
  protected String determineTableName(String tableName, String alias) {
    return alias == null ? tableName : alias;
  }

}
