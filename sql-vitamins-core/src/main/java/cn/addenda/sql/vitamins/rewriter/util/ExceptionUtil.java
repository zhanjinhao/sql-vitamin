/*
 * copy from mybatis project.
 */
package cn.addenda.sql.vitamins.rewriter.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author Clinton Begin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtil {

  public static Throwable unwrapThrowable(Throwable wrapped) {
    if (wrapped == null) {
      return null;
    }
    Throwable unwrapped = wrapped;
    while (true) {
      if (unwrapped instanceof InvocationTargetException) {
        unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
      } else if (unwrapped instanceof UndeclaredThrowableException) {
        unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
      } else {
        return unwrapped;
      }
    }
  }

  public static void reportAsRuntimeException(Throwable throwable, Class<? extends RuntimeException> exception) {
    throwable = ExceptionUtil.unwrapThrowable(throwable);
    if (throwable != null && !(exception.isAssignableFrom(throwable.getClass()))) {
      if (throwable instanceof RuntimeException) {
        throw (RuntimeException) throwable;
      } else {
        throw new UndeclaredThrowableException(throwable);
      }
    }

    throw exception.cast(throwable);
  }

}
