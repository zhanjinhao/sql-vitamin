package cn.addenda.sql.vitamin.rewriter.dynamic;

import cn.addenda.sql.vitamin.rewriter.SqlVitaminException;

/**
 * @author addenda
 * @since 2022/11/26 22:36
 */
public class DynamicSQLException extends SqlVitaminException {
  public DynamicSQLException() {
  }

  public DynamicSQLException(String message) {
    super(message);
  }

  public DynamicSQLException(String message, Throwable cause) {
    super(message, cause);
  }

  public DynamicSQLException(Throwable cause) {
    super(cause);
  }

  public DynamicSQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
