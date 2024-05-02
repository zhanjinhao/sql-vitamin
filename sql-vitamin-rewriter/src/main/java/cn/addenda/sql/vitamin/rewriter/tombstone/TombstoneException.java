package cn.addenda.sql.vitamin.rewriter.tombstone;

import cn.addenda.sql.vitamin.rewriter.SqlVitaminException;

/**
 * @author addenda
 * @since 2023/5/2 17:48
 */
public class TombstoneException extends SqlVitaminException {
  public TombstoneException() {
  }

  public TombstoneException(String message) {
    super(message);
  }

  public TombstoneException(String message, Throwable cause) {
    super(message, cause);
  }

  public TombstoneException(Throwable cause) {
    super(cause);
  }

  public TombstoneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
