package cn.addenda.sql.vitamins.rewriter.sqlcheck;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;

/**
 * @author addenda
 * @since 2023/5/7 15:53
 */
public class SqlCheckException extends SqlVitaminsException {
  public SqlCheckException() {
  }

  public SqlCheckException(String message) {
    super(message);
  }

  public SqlCheckException(String message, Throwable cause) {
    super(message, cause);
  }

  public SqlCheckException(Throwable cause) {
    super(cause);
  }

  public SqlCheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
