package cn.addenda.sql.vitamins.rewriter.lockingreads;

import cn.addenda.sql.vitamins.rewriter.function.TRunnable;
import cn.addenda.sql.vitamins.rewriter.function.TSupplier;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;

/**
 * @author addenda
 * @since 2023/4/27 20:25
 */
public class LockingReadsUtils {

  private LockingReadsUtils() {
  }

  public static <T> T rSelect(TSupplier<T> supplier) {
    LockingReadsContext.push();
    try {
      LockingReadsContext.setLock(LockingReadsContext.R_LOCK);
      return supplier.get();
    } catch (Throwable throwable) {
      ExceptionUtil.reportAsRuntimeException(throwable, LockingReadsException.class);
      return null;
    } finally {
      LockingReadsContext.pop();
    }
  }

  public static <T> T wSelect(TSupplier<T> supplier) {
    LockingReadsContext.push();
    try {
      LockingReadsContext.setLock(LockingReadsContext.W_LOCK);
      return supplier.get();
    } catch (Throwable throwable) {
      ExceptionUtil.reportAsRuntimeException(throwable, LockingReadsException.class);
      return null;
    } finally {
      LockingReadsContext.pop();
    }
  }

  public static <T> T select(String lock, TSupplier<T> supplier) {
    if (LockingReadsContext.W_LOCK.equals(lock)) {
      return wSelect(supplier);
    } else if (LockingReadsContext.R_LOCK.equals(lock)) {
      return rSelect(supplier);
    } else {
      throw new LockingReadsException("不支持的LOCK类型，当前LOCK类型：" + lock + "。");
    }
  }

  public static void rSelect(TRunnable runnable) {
    LockingReadsContext.push();
    try {
      LockingReadsContext.setLock(LockingReadsContext.R_LOCK);
      runnable.run();
    } catch (Throwable throwable) {
      ExceptionUtil.reportAsRuntimeException(throwable, LockingReadsException.class);
    } finally {
      LockingReadsContext.pop();
    }
  }

  public static void wSelect(TRunnable runnable) {
    LockingReadsContext.push();
    try {
      LockingReadsContext.setLock(LockingReadsContext.W_LOCK);
      runnable.run();
    } catch (Throwable throwable) {
      ExceptionUtil.reportAsRuntimeException(throwable, LockingReadsException.class);
    } finally {
      LockingReadsContext.pop();
    }
  }

  public static void select(String lock, TRunnable runnable) {
    if (LockingReadsContext.W_LOCK.equals(lock)) {
      wSelect(runnable);
    } else if (LockingReadsContext.R_LOCK.equals(lock)) {
      rSelect(runnable);
    } else {
      throw new LockingReadsException("不支持的LOCK类型，当前LOCK类型：" + lock + "。");
    }
  }

}
