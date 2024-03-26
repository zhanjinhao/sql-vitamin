package cn.addenda.sql.vitamins.rewriter.dynamicsql;

import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;

import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/5/13 12:23
 */
public class DynamicSQLUtils {

  private DynamicSQLUtils() {
  }

  public static void tableAddJoinCondition(String tableName, String condition, Runnable runnable) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addCondition(DynamicConditionOperation.TABLE_ADD_JOIN_CONDITION, tableName, condition);
      runnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static void tableAddJoinCondition(String condition, Runnable runnable) {
    tableAddJoinCondition(DynamicSQLContext.ALL_TABLE, condition, runnable);
  }

  public static <T> T tableAddJoinCondition(String tableName, String condition, Supplier<T> supplier) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addCondition(DynamicConditionOperation.TABLE_ADD_JOIN_CONDITION, tableName, condition);
      return supplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
      return null;
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static <T> T tableAddJoinCondition(String condition, Supplier<T> supplier) {
    return tableAddJoinCondition(DynamicSQLContext.ALL_TABLE, condition, supplier);
  }

  public static void viewAddJoinCondition(String tableName, String condition, Runnable runnable) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addCondition(DynamicConditionOperation.VIEW_ADD_JOIN_CONDITION, tableName, condition);
      runnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static void viewAddJoinCondition(String condition, Runnable runnable) {
    viewAddJoinCondition(DynamicSQLContext.ALL_TABLE, condition, runnable);
  }

  public static <T> T viewAddJoinCondition(String tableName, String condition, Supplier<T> supplier) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addCondition(DynamicConditionOperation.VIEW_ADD_JOIN_CONDITION, tableName, condition);
      return supplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
      return null;
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static <T> T viewAddJoinCondition(String condition, Supplier<T> supplier) {
    return viewAddJoinCondition(DynamicSQLContext.ALL_TABLE, condition, supplier);
  }

  public static void tableAddWhereCondition(String tableName, String condition, Runnable runnable) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addCondition(DynamicConditionOperation.TABLE_ADD_WHERE_CONDITION, tableName, condition);
      runnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static void tableAddWhereCondition(String condition, Runnable runnable) {
    tableAddWhereCondition(DynamicSQLContext.ALL_TABLE, condition, runnable);
  }

  public static <T> T tableAddWhereCondition(String tableName, String condition, Supplier<T> supplier) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addCondition(DynamicConditionOperation.TABLE_ADD_WHERE_CONDITION, tableName, condition);
      return supplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
      return null;
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static <T> T tableAddWhereCondition(String condition, Supplier<T> supplier) {
    return tableAddWhereCondition(DynamicSQLContext.ALL_TABLE, condition, supplier);
  }

  public static void viewAddWhereCondition(String tableName, String condition, Runnable runnable) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addCondition(DynamicConditionOperation.VIEW_ADD_WHERE_CONDITION, tableName, condition);
      runnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static void viewAddWhereCondition(String condition, Runnable runnable) {
    viewAddWhereCondition(DynamicSQLContext.ALL_TABLE, condition, runnable);
  }

  public static <T> T viewAddWhereCondition(String tableName, String condition, Supplier<T> supplier) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addCondition(DynamicConditionOperation.VIEW_ADD_WHERE_CONDITION, tableName, condition);
      return supplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
      return null;
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static <T> T viewAddWhereCondition(String condition, Supplier<T> supplier) {
    return viewAddWhereCondition(DynamicSQLContext.ALL_TABLE, condition, supplier);
  }

  public static void insertAddItem(String tableName, String itemName, Object itemValue, Runnable runnable) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addItem(DynamicItemOperation.INSERT_ADD_ITEM, tableName, itemName, itemValue);
      runnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static void insertAddItem(String itemName, Object itemValue, Runnable runnable) {
    insertAddItem(DynamicSQLContext.ALL_TABLE, itemName, itemValue, runnable);
  }

  public static <T> T insertAddItem(String tableName, String itemName, Object itemValue, Supplier<T> supplier) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addItem(DynamicItemOperation.INSERT_ADD_ITEM, tableName, itemName, itemValue);
      return supplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
      return null;
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static <T> T insertAddItem(String itemName, Object itemValue, Supplier<T> supplier) {
    return insertAddItem(DynamicSQLContext.ALL_TABLE, itemName, itemValue, supplier);
  }

  public static void updateAddItem(String tableName, String itemName, Object itemValue, Runnable runnable) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addItem(DynamicItemOperation.UPDATE_ADD_ITEM, tableName, itemName, itemValue);
      runnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static void updateAddItem(String itemName, Object itemValue, Runnable runnable) {
    updateAddItem(DynamicSQLContext.ALL_TABLE, itemName, itemValue, runnable);
  }

  public static <T> T updateAddItem(String tableName, String itemName, Object itemValue, Supplier<T> supplier) {
    DynamicSQLContext.push();
    try {
      DynamicSQLContext.addItem(DynamicItemOperation.UPDATE_ADD_ITEM, tableName, itemName, itemValue);
      return supplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
      return null;
    } finally {
      DynamicSQLContext.pop();
    }
  }

  public static <T> T updateAddItem(String itemName, Object itemValue, Supplier<T> supplier) {
    return updateAddItem(DynamicSQLContext.ALL_TABLE, itemName, itemValue, supplier);
  }

}
