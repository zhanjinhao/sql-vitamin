package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2024/4/4 21:14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseEntityUtils {

  public static void baseEntity(BaseEntityConfig baseEntityConfig, Runnable runnable) {
    BaseEntityContext.push(baseEntityConfig);
    try {
      runnable.run();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, BaseEntityException.class);
    } finally {
      BaseEntityContext.pop();
    }
  }

  public static <T> T baseEntity(BaseEntityConfig baseEntityConfig, Supplier<T> supplier) {
    BaseEntityContext.push(baseEntityConfig);
    try {
      return supplier.get();
    } catch (Throwable throwable) {
      throw ExceptionUtil.reportAsRuntimeException(throwable, BaseEntityException.class);
    } finally {
      BaseEntityContext.pop();
    }
  }

}
