package cn.addenda.sql.vitamin.rewriter.tombstone;

import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityException;
import cn.addenda.sql.vitamin.rewriter.AbstractSqlInterceptor;
import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamin.rewriter.util.JdbcSQLUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 只要配置了拦截器就会执行SQL拦截。
 *
 * @author addenda
 * @since 2023/5/2 17:33
 */
@Slf4j
public class TombstoneSqlInterceptor extends AbstractSqlInterceptor {

  private final TombstoneRewriter tombstoneRewriter;
  private final boolean defaultDisable;
  private final boolean defaultCompatibleMode;
  private final boolean defaultJoinUseSubQuery;
  private final boolean defaultIncludeDeleteTime;

  public TombstoneSqlInterceptor(
      boolean removeEnter, TombstoneRewriter tombstoneRewriter,
      boolean defaultDisable, boolean compatibleMode, boolean includeDeleteTime, boolean joinUseSubQuery) {
    super(removeEnter);
    if (tombstoneRewriter == null) {
      throw new TombstoneException("`tombstoneRewriter` can not be null!");
    }
    this.tombstoneRewriter = tombstoneRewriter;
    this.defaultDisable = defaultDisable;
    this.defaultCompatibleMode = compatibleMode;
    this.defaultIncludeDeleteTime = includeDeleteTime;
    this.defaultJoinUseSubQuery = joinUseSubQuery;
  }

  @Override
  public String rewrite(String sql) {

    logDebug("Tombstone, before sql: [{}].", sql);
    if (!TombstoneContext.contextActive()) {
      if (!defaultDisable) {
        try {
          TombstoneContext.push(new TombstoneConfig());
          sql = doRewrite(sql);
        } finally {
          TombstoneContext.pop();
        }
      }
    } else {
      Boolean disable = JdbcSQLUtils.getOrDefault(TombstoneContext.getDisable(), defaultDisable);
      if (Boolean.FALSE.equals(disable)) {
        sql = doRewrite(sql);
      }
    }
    logDebug("Tombstone, after sql: [{}].", sql);
    return sql;
  }

  private String doRewrite(String sql) {
    String newSql = sql;
    try {
      if (JdbcSQLUtils.isDelete(newSql)) {
        Boolean includeDeleteTime =
            JdbcSQLUtils.getOrDefault(TombstoneContext.getIncludeDeleteTime(), defaultIncludeDeleteTime);
        newSql = tombstoneRewriter.rewriteDeleteSql(newSql, includeDeleteTime);
      } else if (JdbcSQLUtils.isSelect(newSql)) {
        Boolean useSubQuery =
            JdbcSQLUtils.getOrDefault(TombstoneContext.getJoinUseSubQuery(), defaultJoinUseSubQuery);
        newSql = tombstoneRewriter.rewriteSelectSql(newSql, useSubQuery);
      } else if (JdbcSQLUtils.isUpdate(newSql)) {
        newSql = tombstoneRewriter.rewriteUpdateSql(newSql);
      } else if (JdbcSQLUtils.isInsert(newSql)) {
        Boolean useSubQuery =
            JdbcSQLUtils.getOrDefault(TombstoneContext.getJoinUseSubQuery(), defaultJoinUseSubQuery);
        newSql = tombstoneRewriter.rewriteInsertSql(newSql, useSubQuery);
      } else {
        // 非CURD语句，有可能是修改数据，此时会影响baseEntity的功能。
        // 兼容模式跳过非CURD数据。非兼容模式抛异常阻止SQL执行。
        Boolean compatibleMode = JdbcSQLUtils.getOrDefault(TombstoneContext.getCompatibleMode(), defaultCompatibleMode);
        String msg = String.format("仅支持select、update、delete、insert语句，当前SQL：[%s]。", removeEnter(sql));
        if (Boolean.TRUE.equals(compatibleMode)) {
          log.info(msg);
        } else {
          throw new BaseEntityException(msg);
        }
      }
    } catch (TombstoneException tombstoneException) {
      throw tombstoneException;
    } catch (Throwable throwable) {
      String msg = String.format("物理删除改写为逻辑删除时出错，SQL：[%s]。", removeEnter(sql));
      throw new TombstoneException(msg, ExceptionUtil.unwrapThrowable(throwable));
    }
    return newSql;
  }

  @Override
  public int order() {
    // baseEntity不会处理delete语句，所以tombstone需要先于baseEntity执行
    return MAX / 2 - 70000;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
