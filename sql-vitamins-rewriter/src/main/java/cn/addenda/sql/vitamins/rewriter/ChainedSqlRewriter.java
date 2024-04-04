package cn.addenda.sql.vitamins.rewriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author addenda
 * @since 2024/3/31 16:11
 */
public class ChainedSqlRewriter implements SqlRewriter {

  public final List<SqlRewriter> sqlRewriterList = new ArrayList<>();

  @Override
  public String rewrite(String sql) {
    for (SqlRewriter sqlRewriter : sqlRewriterList) {
      sql = sqlRewriter.rewrite(sql);
    }
    return sql;
  }

  public ChainedSqlRewriter add(SqlRewriter sqlRewriter) {
    sqlRewriterList.add(sqlRewriter);
    sqlRewriterList.sort(Comparator.comparing(SqlRewriter::order));
    return this;
  }

}
