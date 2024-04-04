package cn.addenda.sql.vitamins.rewriter.tombstone;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlRewriter;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 只要配置了拦截器就会执行SQL拦截。
 *
 * @author addenda
 * @since 2023/5/2 17:33
 */
@Slf4j
public class TombstoneSqlRewriter extends AbstractSqlRewriter {

  private final TombstoneRewriter tombstoneRewriter;
  private final boolean defaultDisable;
  private final boolean defaultJoinUseSubQuery;

  public TombstoneSqlRewriter(
      boolean removeEnter, TombstoneRewriter tombstoneRewriter,
      boolean defaultDisable, boolean joinUseSubQuery) {
    super(removeEnter);
    if (tombstoneRewriter == null) {
      throw new TombstoneException("`tombstoneRewriter` can not be null!");
    }
    this.tombstoneRewriter = tombstoneRewriter;
    this.defaultDisable = defaultDisable;
    this.defaultJoinUseSubQuery = joinUseSubQuery;
  }

  @Override
  public String rewrite(String sql) {
    if (!TombstoneContext.contextActive()) {
      return sql;
    }

    Boolean disable = JdbcSQLUtils.getOrDefault(TombstoneContext.getDisable(), defaultDisable);
    if (Boolean.TRUE.equals(disable)) {
      return sql;
    }
    log.debug("Tombstone, before sql rewriting: [{}].", removeEnter(sql));
    try {
      if (JdbcSQLUtils.isDelete(sql)) {
        sql = tombstoneRewriter.rewriteDeleteSql(sql);
      } else if (JdbcSQLUtils.isSelect(sql)) {
        Boolean useSubQuery =
            JdbcSQLUtils.getOrDefault(TombstoneContext.getJoinUseSubQuery(), defaultJoinUseSubQuery);
        sql = tombstoneRewriter.rewriteSelectSql(sql, useSubQuery);
      } else if (JdbcSQLUtils.isUpdate(sql)) {
        sql = tombstoneRewriter.rewriteUpdateSql(sql);
      } else if (JdbcSQLUtils.isInsert(sql)) {
        Boolean useSubQuery =
            JdbcSQLUtils.getOrDefault(TombstoneContext.getJoinUseSubQuery(), defaultJoinUseSubQuery);
        sql = tombstoneRewriter.rewriteInsertSql(sql, useSubQuery);
      } else {
        throw new TombstoneException("仅支持select、update、delete、insert语句，当前SQL：" + sql + "。");
      }
    } catch (Throwable throwable) {
      String msg = String.format("物理删除改写为逻辑删除时出错，SQL：[%s]。", removeEnter(sql));
      throw new TombstoneException(msg, ExceptionUtil.unwrapThrowable(throwable));
    }
    log.debug("Tombstone, after sql rewriting: [{}].", removeEnter(sql));
    return sql;
  }

  @Override
  public int order() {
    // baseEntity不会处理delete语句，所以tombstone需要先于baseEntity执行
    return MAX / 2 - 70000;
  }

}
