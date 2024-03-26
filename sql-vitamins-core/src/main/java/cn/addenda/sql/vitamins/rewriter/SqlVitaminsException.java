package cn.addenda.sql.vitamins.rewriter;

/**
 * @author addenda
 * @since 2024/3/9 9:56
 */
public class SqlVitaminsException extends RuntimeException {

  public SqlVitaminsException() {
  }

  public SqlVitaminsException(String message) {
    super(message);
  }

  public SqlVitaminsException(String message, Throwable cause) {
    super(message, cause);
  }

  public SqlVitaminsException(Throwable cause) {
    super(cause);
  }

  public SqlVitaminsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
