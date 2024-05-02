package cn.addenda.sql.vitamin.rewriter.dynamic.suffix;

import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/4/27 20:25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicSuffixUtils {

  public static void rSelect(Runnable runnable) {
    suffix(DynamicSuffixContext.getRLock(), runnable);
  }

  public static <T> T rSelect(Supplier<T> supplier) {
    return suffix(DynamicSuffixContext.getRLock(), supplier);
  }

  public static void wSelect(Runnable runnable) {
    suffix(DynamicSuffixContext.getWLock(), runnable);
  }

  public static <T> T wSelect(Supplier<T> supplier) {
    return suffix(DynamicSuffixContext.getWLock(), supplier);
  }

  public static void limit(int limit, Runnable runnable) {
    suffix(DynamicSuffixContext.getLimit(limit), runnable);
  }

  public static <T> T limit(int limit, Supplier<T> supplier) {
    return suffix(DynamicSuffixContext.getLimit(limit), supplier);
  }

  public static void orderBy(String orderBy, Runnable runnable) {
    suffix(DynamicSuffixContext.getOrderBy(orderBy), runnable);
  }

  public static <T> T orderBy(String orderBy, Supplier<T> supplier) {
    return suffix(DynamicSuffixContext.getOrderBy(orderBy), supplier);
  }

  public static void suffix(String suffix, Runnable runnable) {
    DynamicSuffixContext.push(new DynamicSuffixConfig());
    try {
      DynamicSuffixContext.setSuffix(suffix);
      runnable.run();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, DynamicSuffixException.class);
    } finally {
      DynamicSuffixContext.pop();
    }
  }

  public static <T> T suffix(String suffix, Supplier<T> supplier) {
    DynamicSuffixContext.push(new DynamicSuffixConfig());
    try {
      DynamicSuffixContext.setSuffix(suffix);
      return supplier.get();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, DynamicSuffixException.class);
    } finally {
      DynamicSuffixContext.pop();
    }
  }

}
