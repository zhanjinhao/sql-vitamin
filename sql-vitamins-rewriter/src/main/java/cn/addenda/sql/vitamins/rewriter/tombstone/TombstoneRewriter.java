package cn.addenda.sql.vitamins.rewriter.tombstone;

/**
 * @author addenda
 * @since 2023/4/30 19:38
 */
public interface TombstoneRewriter {

  String rewriteInsertSql(String sql, boolean useSubQuery);

  String rewriteDeleteSql(String sql, boolean includeDeleteTime);

  String rewriteSelectSql(String sql, boolean useSubQuery);

  String rewriteUpdateSql(String sql);

}
