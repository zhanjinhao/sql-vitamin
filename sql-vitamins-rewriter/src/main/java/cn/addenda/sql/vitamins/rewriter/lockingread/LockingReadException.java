package cn.addenda.sql.vitamins.rewriter.lockingread;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;

/**
 * @author addenda
 * @since 2022/10/11 19:19
 */
public class LockingReadException extends SqlVitaminsException {
  public LockingReadException() {
  }

  public LockingReadException(String message) {
    super(message);
  }

  public LockingReadException(String message, Throwable cause) {
    super(message, cause);
  }

  public LockingReadException(Throwable cause) {
    super(cause);
  }

  public LockingReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
