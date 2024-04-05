package cn.addenda.sql.vitamins.rewriter.dynamic.tablename;

import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
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
public class DynamicTableNameUtils {

  public static void rename(DynamicTableNameConfigBatch dynamicTableNameConfigBatch, Runnable runnable) {
    DynamicTableNameContext.push(dynamicTableNameConfigBatch);
    try {
      runnable.run();
    } catch (Throwable e) {
      throw ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicTableNameContext.pop();
    }
  }

  public static <T> T rename(DynamicTableNameConfigBatch dynamicTableNameConfigBatch, Supplier<T> supplier) {
    DynamicTableNameContext.push(dynamicTableNameConfigBatch);
    try {
      return supplier.get();
    } catch (Throwable e) {
      throw ExceptionUtil.reportAsRuntimeException(e, DynamicSQLException.class);
    } finally {
      DynamicTableNameContext.pop();
    }
  }

  public static void rename(DynamicTableNameConfig dynamicTableNameConfig, Runnable runnable) {
    rename(DynamicTableNameConfigBatch.of(ArrayUtils.asArrayList(dynamicTableNameConfig)), runnable);
  }

  public static <T> T rename(DynamicTableNameConfig dynamicTableNameConfig, Supplier<T> supplier) {
    return rename(DynamicTableNameConfigBatch.of(ArrayUtils.asArrayList(dynamicTableNameConfig)), supplier);
  }

  public static void rename(String originTableName, String targetTableName, Runnable runnable) {
    DynamicTableNameConfig dynamicTableNameConfig = new DynamicTableNameConfig(
        originTableName, targetTableName);
    List<DynamicTableNameConfig> dynamicTableNameConfigList = new ArrayList<>();
    dynamicTableNameConfigList.add(dynamicTableNameConfig);
    rename(DynamicTableNameConfigBatch.of(dynamicTableNameConfigList), runnable);
  }

  public static <T> T rename(
      String originTableName, String targetTableName, Supplier<T> supplier) {
    DynamicTableNameConfig dynamicTableNameConfig = new DynamicTableNameConfig(
        originTableName, targetTableName);
    List<DynamicTableNameConfig> dynamicTableNameConfigList = new ArrayList<>();
    dynamicTableNameConfigList.add(dynamicTableNameConfig);
    return rename(DynamicTableNameConfigBatch.of(dynamicTableNameConfigList), supplier);
  }

}
