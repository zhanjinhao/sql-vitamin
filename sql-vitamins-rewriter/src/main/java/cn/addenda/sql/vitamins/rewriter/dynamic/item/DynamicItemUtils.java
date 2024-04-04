package cn.addenda.sql.vitamins.rewriter.dynamic.item;

import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
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
public class DynamicItemUtils {

  public static void dynamicItem(DynamicItemConfigBatch dynamicItemConfigBatch, Runnable runnable) {
    DynamicItemContext.push(dynamicItemConfigBatch);
    try {
      runnable.run();
    } catch (Throwable e) {
      throw ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicItemContext.pop();
    }
  }

  public static <T> T dynamicItem(DynamicItemConfigBatch dynamicItemConfigBatch, Supplier<T> supplier) {
    DynamicItemContext.push(dynamicItemConfigBatch);
    try {
      return supplier.get();
    } catch (Throwable e) {
      throw ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicItemContext.pop();
    }
  }

  public static void insertAddItem(
      String tableName, String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate,
      UpdateItemMode updateItemMode, Runnable runnable) {
    DynamicItemConfig dynamicdynamicItemConfigConfig = new DynamicItemConfig(
        DynamicItemOperation.INSERT_ADD_ITEM, tableName, itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
    List<DynamicItemConfig> dynamicItemConfigList = new ArrayList<>();
    dynamicItemConfigList.add(dynamicdynamicItemConfigConfig);
    dynamicItem(DynamicItemConfigBatch.of(dynamicItemConfigList), runnable);
  }

  public static void insertAddItem(
      String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate,
      UpdateItemMode updateItemMode, Runnable runnable) {
    insertAddItem(DynamicItemContext.ALL_TABLE, itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode, runnable);
  }

  public static <T> T insertAddItem(
      String tableName, String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate,
      UpdateItemMode updateItemMode, Supplier<T> supplier) {
    DynamicItemConfig dynamicItemConfig = new DynamicItemConfig(
        DynamicItemOperation.INSERT_ADD_ITEM, tableName, itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
    List<DynamicItemConfig> dynamicItemConfigList = new ArrayList<>();
    dynamicItemConfigList.add(dynamicItemConfig);
    return dynamicItem(DynamicItemConfigBatch.of(dynamicItemConfigList), supplier);
  }

  public static <T> T insertAddItem(
      String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate,
      UpdateItemMode updateItemMode, Supplier<T> supplier) {
    return insertAddItem(DynamicItemContext.ALL_TABLE, itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode, supplier);
  }

  public static void updateAddItem(
      String tableName, String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate,
      UpdateItemMode updateItemMode, Runnable runnable) {
    DynamicItemConfig dynamicItemConfig = new DynamicItemConfig(
        DynamicItemOperation.UPDATE_ADD_ITEM, tableName, itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
    List<DynamicItemConfig> dynamicItemConfigList = new ArrayList<>();
    dynamicItemConfigList.add(dynamicItemConfig);
    dynamicItem(DynamicItemConfigBatch.of(dynamicItemConfigList), runnable);
  }

  public static void updateAddItem(
      String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate,
      UpdateItemMode updateItemMode, Runnable runnable) {
    updateAddItem(DynamicItemContext.ALL_TABLE, itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode, runnable);
  }

  public static <T> T updateAddItem(
      String tableName, String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate,
      UpdateItemMode updateItemMode, Supplier<T> supplier) {
    DynamicItemConfig dynamicItemConfig = new DynamicItemConfig(
        DynamicItemOperation.UPDATE_ADD_ITEM, tableName, itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
    List<DynamicItemConfig> dynamicItemConfigList = new ArrayList<>();
    dynamicItemConfigList.add(dynamicItemConfig);
    return dynamicItem(DynamicItemConfigBatch.of(dynamicItemConfigList), supplier);
  }

  public static <T> T updateAddItem(
      String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate,
      UpdateItemMode updateItemMode, Supplier<T> supplier) {
    return updateAddItem(DynamicItemContext.ALL_TABLE, itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode, supplier);
  }

}
