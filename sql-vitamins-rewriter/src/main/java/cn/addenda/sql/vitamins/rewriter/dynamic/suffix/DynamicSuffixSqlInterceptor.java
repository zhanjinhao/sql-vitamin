package cn.addenda.sql.vitamins.rewriter.dynamic.suffix;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 配置了拦截器 且 DynamicSuffixContext 配置了suffix 才会执行。
 *
 * @author addenda
 * @since 2023/4/27 20:19
 */
@Slf4j
public class DynamicSuffixSqlInterceptor extends AbstractSqlInterceptor {

  public DynamicSuffixSqlInterceptor() {
  }

  public DynamicSuffixSqlInterceptor(boolean removeEnter) {
    super(removeEnter);
  }

  @Override
  public String rewrite(String sql) {
    if (!DynamicSuffixContext.contextActive()) {
      return sql;
    }
    String suffix = DynamicSuffixContext.getSuffix();
    if (suffix == null) {
      return sql;
    }

    logDebug("Dynamic Suffix, before sql: [{}].", sql);
    sql = trim(sql) + " " + suffix;
    logDebug("Dynamic Suffix, after sql: [{}].", sql);
    return sql;
  }

  private String trim(String sql) {
    sql = sql.trim();
    if (sql.endsWith(";")) {
      sql = sql.substring(0, sql.length() - 1);
    }
    return sql;
  }

  @Override
  public int order() {
    return MAX / 2 - 80000;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
