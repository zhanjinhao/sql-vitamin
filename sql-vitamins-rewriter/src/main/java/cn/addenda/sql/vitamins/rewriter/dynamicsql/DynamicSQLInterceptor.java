package cn.addenda.sql.vitamins.rewriter.dynamicsql;

import cn.addenda.sql.vitamins.rewriter.ConnectionPrepareStatementInterceptor;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.Item;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 配置了拦截器 且 DynamicSQLContext配置了条件 才会执行。
 *
 * @author addenda
 * @since 2023/4/30 16:30
 */
@Slf4j
public class DynamicSQLInterceptor extends ConnectionPrepareStatementInterceptor {

  private final DynamicRewriter dynamicRewriter;
  private final InsertAddSelectItemMode defaultInsertAddSelectItemMode;
  private final boolean defaultDuplicateKeyUpdate;
  private final UpdateItemMode defaultUpdateItemMode;
  private final boolean defaultJoinUseSubQuery;

  public DynamicSQLInterceptor(boolean removeEnter, DynamicRewriter dynamicRewriter, InsertAddSelectItemMode insertAddSelectItemMode,
                               boolean duplicateKeyUpdate, UpdateItemMode updateItemMode, boolean joinUseSubQuery) {
    super(removeEnter);
    this.dynamicRewriter = dynamicRewriter;
    this.defaultInsertAddSelectItemMode = insertAddSelectItemMode == null ? InsertAddSelectItemMode.VALUE : insertAddSelectItemMode;
    this.defaultDuplicateKeyUpdate = duplicateKeyUpdate;
    this.defaultUpdateItemMode = updateItemMode == null ? UpdateItemMode.NOT_NULL : updateItemMode;
    this.defaultJoinUseSubQuery = joinUseSubQuery;
  }

  public DynamicSQLInterceptor() {
    this(true, new DruidDynamicRewriter(new DefaultDataConvertorRegistry()), InsertAddSelectItemMode.VALUE, false,
        UpdateItemMode.NOT_NULL, false);
  }

  protected String process(String sql) {
    if (!DynamicSQLContext.contextActive()) {
      return sql;
    }

    Map<String, List<Map.Entry<DynamicConditionOperation, String>>> conditionMap = DynamicSQLContext.getConditionMap();
    Map<String, List<Map.Entry<DynamicItemOperation, Item>>> itemMap = DynamicSQLContext.getItemMap();

    if (conditionMap == null && itemMap == null) {
      return sql;
    }
    log.debug("Dynamic Condition, before sql rewriting: [{}].", removeEnter(sql));
    String newSql;
    try {
      newSql = doProcess(removeEnter(sql), conditionMap, itemMap);
    } catch (Throwable throwable) {
      String msg = String.format("拼装动态条件时出错，SQL：[%s]，conditionMap: [%s]，itemMap：[%s]。", removeEnter(sql), conditionMap, itemMap);
      throw new DynamicSQLException(msg, ExceptionUtil.unwrapThrowable(throwable));
    }

    log.debug("Dynamic Condition, after sql rewriting: [{}].", newSql);
    return newSql;
  }

  private String doProcess(String sql, Map<String, List<Map.Entry<DynamicConditionOperation, String>>> conditionMap,
                           Map<String, List<Map.Entry<DynamicItemOperation, Item>>> itemMap) {
    String newSql = sql;

    // condition 过滤条件
    if (conditionMap != null && !conditionMap.isEmpty()) {
      for (Map.Entry<String, List<Map.Entry<DynamicConditionOperation, String>>> tableEntry : conditionMap.entrySet()) {
        String tableName = tableEntry.getKey();
        if (DynamicSQLContext.ALL_TABLE.equals(tableName)) {
          tableName = null;
        }
        for (Map.Entry<DynamicConditionOperation, String> operationEntry : tableEntry.getValue()) {
          DynamicConditionOperation operation = operationEntry.getKey();
          String condition = operationEntry.getValue();
          if (DynamicConditionOperation.TABLE_ADD_JOIN_CONDITION.equals(operation) && !JdbcSQLUtils.isInsert(newSql)) {
            Boolean useSubQuery =
                JdbcSQLUtils.getOrDefault(DynamicSQLContext.getJoinUseSubQuery(), defaultJoinUseSubQuery);
            newSql = dynamicRewriter.tableAddJoinCondition(newSql, tableName, condition, useSubQuery);
          } else if (DynamicConditionOperation.VIEW_ADD_JOIN_CONDITION.equals(operation) && !JdbcSQLUtils.isInsert(newSql)) {
            Boolean useSubQuery =
                JdbcSQLUtils.getOrDefault(DynamicSQLContext.getJoinUseSubQuery(), defaultJoinUseSubQuery);
            newSql = dynamicRewriter.viewAddJoinCondition(newSql, tableName, condition, useSubQuery);
          } else if (DynamicConditionOperation.TABLE_ADD_WHERE_CONDITION.equals(operation) && !JdbcSQLUtils.isInsert(newSql)) {
            newSql = dynamicRewriter.tableAddWhereCondition(newSql, tableName, condition);
          } else if (DynamicConditionOperation.VIEW_ADD_WHERE_CONDITION.equals(operation) && !JdbcSQLUtils.isInsert(newSql)) {
            newSql = dynamicRewriter.viewAddWhereCondition(newSql, tableName, condition);
          } else {
            String msg = String.format("不支持的SQL添加条件操作类型：[%s]，SQL：[%s]。", operation, removeEnter(sql));
            throw new UnsupportedOperationException(msg);
          }
        }
      }
    }

    // item 过滤条件
    if (itemMap != null && !itemMap.isEmpty()) {
      for (Map.Entry<String, List<Map.Entry<DynamicItemOperation, Item>>> tableEntry : itemMap.entrySet()) {
        String tableName = tableEntry.getKey();
        if (DynamicSQLContext.ALL_TABLE.equals(tableName)) {
          tableName = null;
        }
        for (Map.Entry<DynamicItemOperation, Item> operationEntry : tableEntry.getValue()) {
          DynamicItemOperation operation = operationEntry.getKey();
          Item item = operationEntry.getValue();
          UpdateItemMode updateItemMode =
              JdbcSQLUtils.getOrDefault(DynamicSQLContext.getUpdateItemMode(), defaultUpdateItemMode);
          if (DynamicItemOperation.INSERT_ADD_ITEM.equals(operation) && JdbcSQLUtils.isInsert(newSql)) {
            Boolean duplicateKeyUpdate =
                JdbcSQLUtils.getOrDefault(DynamicSQLContext.getDuplicateKeyUpdate(), defaultDuplicateKeyUpdate);
            InsertAddSelectItemMode insertAddSelectItemMode =
                JdbcSQLUtils.getOrDefault(DynamicSQLContext.getInsertSelectAddItemMode(), defaultInsertAddSelectItemMode);
            newSql = dynamicRewriter.insertAddItem(newSql, tableName, item, insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
          } else if (DynamicItemOperation.UPDATE_ADD_ITEM.equals(operation) && JdbcSQLUtils.isUpdate(newSql)) {
            newSql = dynamicRewriter.updateAddItem(newSql, tableName, item, updateItemMode);
          } else {
            String msg = String.format("不支持的SQL添加item作类型：[%s]，SQL：[%s]。", operation, removeEnter(sql));
            throw new UnsupportedOperationException(msg);
          }
        }

      }
    }

    return newSql;
  }

  @Override
  public int order() {
    return MAX / 2 - 60000;
  }

}
