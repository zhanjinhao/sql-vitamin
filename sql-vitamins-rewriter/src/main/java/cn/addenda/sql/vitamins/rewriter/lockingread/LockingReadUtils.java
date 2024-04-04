package cn.addenda.sql.vitamins.rewriter.lockingread;

import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/4/27 20:25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LockingReadUtils {

  public static void rSelect(Runnable runnable) {
    LockingReadContext.push(new LockingReadConfig());
    try {
      LockingReadContext.setLock(LockingReadContext.R_LOCK);
      runnable.run();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, LockingReadException.class);
    } finally {
      LockingReadContext.pop();
    }
  }

  public static <T> T rSelect(Supplier<T> supplier) {
    LockingReadContext.push(new LockingReadConfig());
    try {
      LockingReadContext.setLock(LockingReadContext.R_LOCK);
      return supplier.get();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, LockingReadException.class);
    } finally {
      LockingReadContext.pop();
    }
  }

  public static void wSelect(Runnable runnable) {
    LockingReadContext.push(new LockingReadConfig());
    try {
      LockingReadContext.setLock(LockingReadContext.W_LOCK);
      runnable.run();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, LockingReadException.class);
    } finally {
      LockingReadContext.pop();
    }
  }

  public static <T> T wSelect(Supplier<T> supplier) {
    LockingReadContext.push(new LockingReadConfig());
    try {
      LockingReadContext.setLock(LockingReadContext.W_LOCK);
      return supplier.get();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, LockingReadException.class);
    } finally {
      LockingReadContext.pop();
    }
  }

  public static void select(String lock, Runnable runnable) {
    if (LockingReadContext.W_LOCK.equals(lock)) {
      wSelect(runnable);
    } else if (LockingReadContext.R_LOCK.equals(lock)) {
      rSelect(runnable);
    } else {
      throw new LockingReadException("不支持的LOCK类型，当前LOCK类型：" + lock + "。");
    }
  }

  public static <T> T select(String lock, Supplier<T> supplier) {
    if (LockingReadContext.W_LOCK.equals(lock)) {
      return wSelect(supplier);
    } else if (LockingReadContext.R_LOCK.equals(lock)) {
      return rSelect(supplier);
    } else {
      throw new LockingReadException("不支持的LOCK类型，当前LOCK类型：" + lock + "。");
    }
  }

}
