package cn.addenda.sql.vitamins.rewriter;

import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;

/**
 * @author addenda
 * @since 2024/3/31 16:06
 */
public abstract class AbstractSqlRewriter implements SqlRewriter {

  private final boolean removeEnter;

  protected AbstractSqlRewriter() {
    this.removeEnter = true;
  }

  protected AbstractSqlRewriter(boolean removeEnter) {
    this.removeEnter = removeEnter;
  }

  protected String removeEnter(String sql) {
    if (removeEnter) {
      return DruidSQLUtils.removeEnter(sql);
    }
    return sql;
  }

}
