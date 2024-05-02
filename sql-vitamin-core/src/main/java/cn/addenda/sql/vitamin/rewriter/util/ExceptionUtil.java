/*
 * copy from mybatis project.
 */
package cn.addenda.sql.vitamin.rewriter.util;

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

  public static RuntimeException reportAsRuntimeException(Throwable throwable, Class<? extends RuntimeException> exception) {
    throwable = ExceptionUtil.unwrapThrowable(throwable);
    if (throwable != null && !(exception.isAssignableFrom(throwable.getClass()))) {
      if (throwable instanceof RuntimeException) {
        return (RuntimeException) throwable;
      } else {
        return new UndeclaredThrowableException(throwable);
      }
    }

    return exception.cast(throwable);
  }

}
