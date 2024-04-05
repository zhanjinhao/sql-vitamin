package cn.addenda.sql.vitamins.rewriter.tombstone;

import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/5/28 17:56
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TombstoneUtils {

  public static void tombstone(TombstoneConfig tombstoneConfig, Runnable runnable) {
    TombstoneContext.push(tombstoneConfig);
    try {
      runnable.run();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, TombstoneException.class);
    } finally {
      TombstoneContext.pop();
    }
  }

  public static <T> T tombstone(TombstoneConfig tombstoneConfig, Supplier<T> supplier) {
    TombstoneContext.push(tombstoneConfig);
    try {
      return supplier.get();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, TombstoneException.class);
    } finally {
      TombstoneContext.pop();
    }
  }

}
