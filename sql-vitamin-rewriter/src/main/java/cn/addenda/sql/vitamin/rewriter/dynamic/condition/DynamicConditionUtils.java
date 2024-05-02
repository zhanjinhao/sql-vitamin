package cn.addenda.sql.vitamin.rewriter.dynamic.condition;

import cn.addenda.sql.vitamin.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamin.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/5/13 12:23
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicConditionUtils {

  public static void dynamicCondition(DynamicConditionConfigBatch dynamicConditionConfigBatch, Runnable runnable) {
    DynamicConditionContext.push(dynamicConditionConfigBatch);
    try {
      runnable.run();
    } catch (Throwable e) {
      throw ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicConditionContext.pop();
    }
  }

  public static <T> T dynamicCondition(DynamicConditionConfigBatch dynamicConditionConfigBatch, Supplier<T> supplier) {
    DynamicConditionContext.push(dynamicConditionConfigBatch);
    try {
      return supplier.get();
    } catch (Throwable e) {
      throw ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicConditionContext.pop();
    }
  }

  public static void dynamicCondition(DynamicConditionConfig dynamicConditionConfig, Runnable runnable) {
    dynamicCondition(DynamicConditionConfigBatch.of(ArrayUtils.asArrayList(dynamicConditionConfig)), runnable);
  }

  public static <T> T dynamicCondition(DynamicConditionConfig dynamicConditionConfig, Supplier<T> supplier) {
    return dynamicCondition(DynamicConditionConfigBatch.of(ArrayUtils.asArrayList(dynamicConditionConfig)), supplier);
  }

  public static void tableAddJoinCondition(String tableOrViewName, String condition, boolean joinUseSubQuery, Runnable runnable) {
    DynamicConditionConfig dynamicConditionConfig
        = DynamicConditionConfig.of(DynamicConditionOperation.TABLE_ADD_JOIN_CONDITION, tableOrViewName, condition, joinUseSubQuery);
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.add(dynamicConditionConfig);
    dynamicCondition(DynamicConditionConfigBatch.of(dynamicConditionConfigList), runnable);
  }

  public static void tableAddJoinCondition(String condition, boolean joinUseSubQuery, Runnable runnable) {
    tableAddJoinCondition(DynamicConditionContext.ALL_TABLE, condition, joinUseSubQuery, runnable);
  }

  public static <T> T tableAddJoinCondition(String tableOrViewName, String condition, boolean joinUseSubQuery, Supplier<T> supplier) {
    DynamicConditionConfig dynamicConditionConfig
        = DynamicConditionConfig.of(DynamicConditionOperation.TABLE_ADD_JOIN_CONDITION, tableOrViewName, condition, joinUseSubQuery);
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.add(dynamicConditionConfig);
    return dynamicCondition(DynamicConditionConfigBatch.of(dynamicConditionConfigList), supplier);
  }

  public static <T> T tableAddJoinCondition(String condition, boolean joinUseSubQuery, Supplier<T> supplier) {
    return tableAddJoinCondition(DynamicConditionContext.ALL_TABLE, condition, joinUseSubQuery, supplier);
  }

  public static void viewAddJoinCondition(String tableOrViewName, String condition, boolean joinUseSubQuery, Runnable runnable) {
    DynamicConditionConfig dynamicConditionConfig
        = DynamicConditionConfig.of(DynamicConditionOperation.VIEW_ADD_JOIN_CONDITION, tableOrViewName, condition, joinUseSubQuery);
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.add(dynamicConditionConfig);
    dynamicCondition(DynamicConditionConfigBatch.of(dynamicConditionConfigList), runnable);
  }

  public static void viewAddJoinCondition(String condition, boolean joinUseSubQuery, Runnable runnable) {
    viewAddJoinCondition(DynamicConditionContext.ALL_TABLE, condition, joinUseSubQuery, runnable);
  }

  public static <T> T viewAddJoinCondition(String tableOrViewName, String condition, boolean joinUseSubQuery, Supplier<T> supplier) {
    DynamicConditionConfig dynamicConditionConfig
        = DynamicConditionConfig.of(DynamicConditionOperation.VIEW_ADD_JOIN_CONDITION, tableOrViewName, condition, joinUseSubQuery);
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.add(dynamicConditionConfig);
    return dynamicCondition(DynamicConditionConfigBatch.of(dynamicConditionConfigList), supplier);
  }

  public static <T> T viewAddJoinCondition(String condition, boolean joinUseSubQuery, Supplier<T> supplier) {
    return viewAddJoinCondition(DynamicConditionContext.ALL_TABLE, condition, joinUseSubQuery, supplier);
  }

  public static void tableAddWhereCondition(String tableOrViewName, String condition, Runnable runnable) {
    DynamicConditionConfig dynamicConditionConfig
        = DynamicConditionConfig.of(DynamicConditionOperation.TABLE_ADD_WHERE_CONDITION, tableOrViewName, condition, null);
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.add(dynamicConditionConfig);
    dynamicCondition(DynamicConditionConfigBatch.of(dynamicConditionConfigList), runnable);
  }

  public static void tableAddWhereCondition(String condition, Runnable runnable) {
    tableAddWhereCondition(DynamicConditionContext.ALL_TABLE, condition, runnable);
  }

  public static <T> T tableAddWhereCondition(String tableOrViewName, String condition, Supplier<T> supplier) {
    DynamicConditionConfig dynamicConditionConfig
        = DynamicConditionConfig.of(DynamicConditionOperation.TABLE_ADD_WHERE_CONDITION, tableOrViewName, condition, null);
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.add(dynamicConditionConfig);
    return dynamicCondition(DynamicConditionConfigBatch.of(dynamicConditionConfigList), supplier);
  }

  public static <T> T tableAddWhereCondition(String condition, Supplier<T> supplier) {
    return tableAddWhereCondition(DynamicConditionContext.ALL_TABLE, condition, supplier);
  }

  public static void viewAddWhereCondition(String tableOrViewName, String condition, Runnable runnable) {
    DynamicConditionConfig dynamicConditionConfig
        = DynamicConditionConfig.of(DynamicConditionOperation.VIEW_ADD_WHERE_CONDITION, tableOrViewName, condition, null);
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.add(dynamicConditionConfig);
    dynamicCondition(DynamicConditionConfigBatch.of(dynamicConditionConfigList), runnable);
  }

  public static void viewAddWhereCondition(String condition, Runnable runnable) {
    viewAddWhereCondition(DynamicConditionContext.ALL_TABLE, condition, runnable);
  }

  public static <T> T viewAddWhereCondition(String tableOrViewName, String condition, Supplier<T> supplier) {
    DynamicConditionConfig dynamicConditionConfig
        = DynamicConditionConfig.of(DynamicConditionOperation.VIEW_ADD_WHERE_CONDITION, tableOrViewName, condition, null);
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.add(dynamicConditionConfig);
    return dynamicCondition(DynamicConditionConfigBatch.of(dynamicConditionConfigList), supplier);
  }

  public static <T> T viewAddWhereCondition(String condition, Supplier<T> supplier) {
    return viewAddWhereCondition(DynamicConditionContext.ALL_TABLE, condition, supplier);
  }

}
