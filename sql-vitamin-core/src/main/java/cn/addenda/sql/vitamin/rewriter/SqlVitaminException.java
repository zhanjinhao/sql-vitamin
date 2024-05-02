package cn.addenda.sql.vitamin.rewriter;

/**
 * @author addenda
 * @since 2024/3/9 9:56
 */
public class SqlVitaminException extends RuntimeException {

  public SqlVitaminException() {
  }

  public SqlVitaminException(String message) {
    super(message);
  }

  public SqlVitaminException(String message, Throwable cause) {
    super(message, cause);
  }

  public SqlVitaminException(Throwable cause) {
    super(cause);
  }

  public SqlVitaminException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
