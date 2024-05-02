package cn.addenda.sql.vitamin.rewriter.dynamic.suffix;

import cn.addenda.sql.vitamin.rewriter.SqlVitaminException;

/**
 * @author addenda
 * @since 2022/10/11 19:19
 */
public class DynamicSuffixException extends SqlVitaminException {
  public DynamicSuffixException() {
  }

  public DynamicSuffixException(String message) {
    super(message);
  }

  public DynamicSuffixException(String message, Throwable cause) {
    super(message, cause);
  }

  public DynamicSuffixException(Throwable cause) {
    super(cause);
  }

  public DynamicSuffixException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
