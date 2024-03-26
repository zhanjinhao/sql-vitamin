package cn.addenda.sql.vitamins.rewriter.sqlcheck;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;

/**
 * @author addenda
 * @since 2023/5/7 15:53
 */
public class SQLCheckException extends SqlVitaminsException {
  public SQLCheckException() {
  }

  public SQLCheckException(String message) {
    super(message);
  }

  public SQLCheckException(String message, Throwable cause) {
    super(message, cause);
  }

  public SQLCheckException(Throwable cause) {
    super(cause);
  }

  public SQLCheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
