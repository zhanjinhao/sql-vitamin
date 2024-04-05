package cn.addenda.sql.vitamins.rewriter.dynamic.condition;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlInterceptor;
import cn.addenda.sql.vitamins.rewriter.dynamic.*;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneException;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.List;

/**
 * 配置了拦截器 且 DynamicConditionContext配置了条件 才会执行。
 *
 * @author addenda
 * @since 2023/4/30 16:30
 */
@Slf4j
public class DynamicConditionSqlInterceptor extends AbstractSqlInterceptor {

  private final boolean defaultJoinUseSubQuery;
  private final DynamicConditionRewriter dynamicConditionRewriter;

  public DynamicConditionSqlInterceptor(
      boolean removeEnter, boolean joinUseSubQuery, DynamicConditionRewriter dynamicConditionRewriter) {
    super(removeEnter);
    this.defaultJoinUseSubQuery = joinUseSubQuery;
    this.dynamicConditionRewriter = dynamicConditionRewriter;
  }

  @Override
  public String rewrite(String sql) {
    if (!DynamicConditionContext.contextActive()) {
      return sql;
    }

    List<DynamicConditionConfig> dynamicConditionConfigList = DynamicConditionContext.getDynamicConditionConfigList();
    if (dynamicConditionConfigList == null || dynamicConditionConfigList.isEmpty()) {
      return sql;
    }
    logDebug("Dynamic Condition, before sql: [{}].", sql);
    sql = doRewrite(sql, dynamicConditionConfigList);
    logDebug("Dynamic Condition, after sql: [{}].", sql);
    return sql;
  }

  private String doRewrite(String sql, List<DynamicConditionConfig> dynamicConditionConfigList) {
    String newSql = sql;

    for (DynamicConditionConfig dynamicConditionConfig : dynamicConditionConfigList) {
      DynamicConditionOperation dynamicConditionOperation = dynamicConditionConfig.getDynamicConditionOperation();
      String tableOrViewName = dynamicConditionConfig.getTableOrViewName();
      if (DynamicConditionContext.ALL_TABLE.equals(tableOrViewName)) {
        tableOrViewName = null;
      }
      String condition = dynamicConditionConfig.getCondition();
      Boolean joinUseSubQuery = JdbcSQLUtils.getOrDefault(dynamicConditionConfig.getJoinUseSubQuery(), defaultJoinUseSubQuery);

      try {
        if (DynamicConditionOperation.TABLE_ADD_JOIN_CONDITION == dynamicConditionOperation) {
          if (!JdbcSQLUtils.isInsert(newSql)) {
            newSql = dynamicConditionRewriter.tableAddJoinCondition(newSql, tableOrViewName, condition, joinUseSubQuery);
          }
        } else if (DynamicConditionOperation.VIEW_ADD_JOIN_CONDITION == dynamicConditionOperation) {
          if (!JdbcSQLUtils.isInsert(newSql)) {
            newSql = dynamicConditionRewriter.viewAddJoinCondition(newSql, tableOrViewName, condition, joinUseSubQuery);
          }
        } else if (DynamicConditionOperation.TABLE_ADD_WHERE_CONDITION == dynamicConditionOperation) {
          if (!JdbcSQLUtils.isInsert(newSql)) {
            newSql = dynamicConditionRewriter.tableAddWhereCondition(newSql, tableOrViewName, condition);
          }
        } else if (DynamicConditionOperation.VIEW_ADD_WHERE_CONDITION == dynamicConditionOperation) {
          if (!JdbcSQLUtils.isInsert(newSql)) {
            newSql = dynamicConditionRewriter.viewAddWhereCondition(newSql, tableOrViewName, condition);
          }
        } else {
          String msg = String.format("不支持的操作类型：[%s]，SQL：[%s]。", dynamicConditionOperation, removeEnter(sql));
          throw new DynamicSQLException(msg);
        }
      } catch (DynamicSQLException dynamicSQLException) {
        throw dynamicSQLException;
      } catch (Throwable throwable) {
        String msg = String.format("增加动态条件时出错，SQL：[%s]。", removeEnter(sql));
        throw new TombstoneException(msg, ExceptionUtil.unwrapThrowable(throwable));
      }

    }

    return newSql;
  }

  @Override
  public int order() {
    return MAX / 2 - 61000;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
