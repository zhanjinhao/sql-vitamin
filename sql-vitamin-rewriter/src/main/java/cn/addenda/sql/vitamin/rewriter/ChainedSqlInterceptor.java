package cn.addenda.sql.vitamin.rewriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author addenda
 * @since 2024/3/31 16:11
 */
public class ChainedSqlInterceptor implements SqlInterceptor {

  public final List<SqlInterceptor> sqlInterceptorList = new ArrayList<>();

  @Override
  public String rewrite(String sql) {
    for (SqlInterceptor sqlInterceptor : sqlInterceptorList) {
      sql = sqlInterceptor.rewrite(sql);
    }
    return sql;
  }

  public ChainedSqlInterceptor add(SqlInterceptor sqlInterceptor) {
    sqlInterceptorList.add(sqlInterceptor);
    sqlInterceptorList.sort(Comparator.comparing(SqlInterceptor::order));
    return this;
  }

}
