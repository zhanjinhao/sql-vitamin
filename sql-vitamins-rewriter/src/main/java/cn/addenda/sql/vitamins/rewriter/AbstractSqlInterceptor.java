package cn.addenda.sql.vitamins.rewriter;

import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;

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

  protected String removeEnter(String sql) {
    if (removeEnter) {
      return DruidSQLUtils.removeEnter(sql);
    }
    return sql;
  }

}
