package cn.addenda.sql.vitamins.rewriter;

import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import org.slf4j.Logger;

/**
 * @author addenda
 * @since 2024/3/31 16:06
 */
public abstract class AbstractSqlInterceptor implements SqlInterceptor {

  private final boolean removeEnter;

  protected AbstractSqlInterceptor() {
    this.removeEnter = true;
  }

  protected AbstractSqlInterceptor(boolean removeEnter) {
    this.removeEnter = removeEnter;
  }

  /**
   * 仅在打日志的时候使用，以方便排查问题
   */
  protected String removeEnter(String sql) {
    if (removeEnter) {
      return DruidSQLUtils.removeEnter(sql);
    }
    return sql;
  }

  protected void logDebug(String format, String sql) {
    Logger logger = getLogger();
    if (logger.isDebugEnabled()) {
      logger.debug(format, removeEnter(sql));
    }
  }

  abstract protected Logger getLogger();

}
