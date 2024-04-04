package cn.addenda.sql.vitamins.rewriter.dynamic.condition;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.*;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 配置了拦截器 且 DynamicConditionContext配置了条件 才会执行。
 *
 * @author addenda
 * @since 2023/4/30 16:30
 */
@Slf4j
public class DynamicConditionSqlRewriter extends AbstractSqlRewriter {

  private final DynamicConditionRewriter dynamicConditionRewriter;

  public DynamicConditionSqlRewriter(boolean removeEnter, DynamicConditionRewriter dynamicConditionRewriter) {
    super(removeEnter);
    this.dynamicConditionRewriter = dynamicConditionRewriter;
  }

  @Override
  public String rewrite(String sql) {
    if (!DynamicConditionContext.contextActive()) {
      return sql;
    }

    List<DynamicConditionConfig> dynamicConditionConfigList = DynamicConditionContext.getDynamicConditionConfigList();
    if (dynamicConditionConfigList == null) {
      return sql;
    }
    log.debug("Dynamic Condition, before sql rewriting: [{}].", removeEnter(sql));
    String newSql;
    try {
      newSql = doProcess(removeEnter(sql), dynamicConditionConfigList);
    } catch (Throwable throwable) {
      String msg = String.format("拼装动态SQL时出错，SQL：[%s]，conditionList: [%s]。", removeEnter(sql), dynamicConditionConfigList);
      throw new DynamicSQLException(msg, ExceptionUtil.unwrapThrowable(throwable));
    }

    log.debug("Dynamic Condition, after sql rewriting: [{}].", newSql);
    return newSql;
  }

  private String doProcess(String sql, List<DynamicConditionConfig> dynamicConditionConfigList) {
    String newSql = sql;

    for (DynamicConditionConfig dynamicConditionConfig : dynamicConditionConfigList) {
      DynamicConditionOperation dynamicConditionOperation = dynamicConditionConfig.getDynamicConditionOperation();
      String tableOrViewName = dynamicConditionConfig.getTableOrViewName();
      if (DynamicConditionContext.ALL_TABLE.equals(tableOrViewName)) {
        tableOrViewName = null;
      }
      String condition = dynamicConditionConfig.getCondition();
      Boolean joinUseSubQuery = dynamicConditionConfig.getJoinUseSubQuery();
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
        throw new UnsupportedOperationException(msg);
      }
    }

    return newSql;
  }

  @Override
  public int order() {
    return MAX / 2 - 60000;
  }

}
