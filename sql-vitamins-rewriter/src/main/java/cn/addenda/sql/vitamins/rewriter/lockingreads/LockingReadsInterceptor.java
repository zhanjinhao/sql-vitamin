package cn.addenda.sql.vitamins.rewriter.lockingreads;

import cn.addenda.sql.vitamins.rewriter.ConnectionPrepareStatementInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置了拦截器 且 LockingReadsContext配置了锁 才会执行。
 *
 * @author addenda
 * @since 2023/4/27 20:19
 */
@Slf4j
public class LockingReadsInterceptor extends ConnectionPrepareStatementInterceptor {

  public LockingReadsInterceptor() {
  }

  public LockingReadsInterceptor(boolean removeEnter) {
    super(removeEnter);
  }

  protected String process(String sql) {
    if (!LockingReadsContext.contextActive()) {
      return sql;
    }
    String lock = LockingReadsContext.getLock();
    if (lock == null) {
      return sql;
    }

    log.debug("Locking Reads, before sql rewriting: [{}].", removeEnter(sql));
    if (LockingReadsContext.R_LOCK.equals(lock)) {
      sql = sql + " lock in share mode";
    } else if (LockingReadsContext.W_LOCK.equals(lock)) {
      sql = sql + " for update";
    } else {
      String msg = String.format("不支持的Lock类型，SQL：[%s]，当前Lock类型：[%s]。", removeEnter(sql), lock);
      throw new LockingReadsException(msg);
    }

    log.debug("Locking Reads, after sql rewriting: [{}].", removeEnter(sql));
    return sql;
  }

  @Override
  public int order() {
    return MAX / 2 - 80000;
  }
}
