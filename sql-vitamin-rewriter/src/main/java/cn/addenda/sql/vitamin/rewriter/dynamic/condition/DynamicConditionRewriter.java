package cn.addenda.sql.vitamin.rewriter.dynamic.condition;

/**
 * @author addenda
 * @since 2023/4/30 16:56
 */
public interface DynamicConditionRewriter {

  String tableAddJoinCondition(String sql, String tableOrViewName, String condition, boolean useSubQuery);

  String viewAddJoinCondition(String sql, String tableOrViewName, String condition, boolean useSubQuery);

  String tableAddWhereCondition(String sql, String tableOrViewName, String condition);

  String viewAddWhereCondition(String sql, String tableOrViewName, String condition);

}
