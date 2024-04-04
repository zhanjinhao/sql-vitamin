package cn.addenda.sql.vitamins.rewriter.lockingread;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * todo 改造成SQL suffix
 * 配置了拦截器 且 LockingReadContext配置了锁 才会执行。
 *
 * @author addenda
 * @since 2023/4/27 20:19
 */
@Slf4j
public class LockingReadSqlInterceptor extends AbstractSqlInterceptor {

  public LockingReadSqlInterceptor() {
  }

  public LockingReadSqlInterceptor(boolean removeEnter) {
    super(removeEnter);
  }

  @Override
  public String rewrite(String sql) {
    if (!LockingReadContext.contextActive()) {
      return sql;
    }
    String lock = LockingReadContext.getLock();
    if (lock == null) {
      return sql;
    }

    log.debug("Locking Reads, before sql: [{}].", removeEnter(sql));
    // todo 移除sql末位的;
    if (LockingReadContext.R_LOCK.equals(lock)) {
      sql = sql + " lock in share mode";
    } else if (LockingReadContext.W_LOCK.equals(lock)) {
      sql = sql + " for update";
    } else {
      String msg = String.format("不支持的Lock类型，SQL：[%s]，当前Lock类型：[%s]。", removeEnter(sql), lock);
      throw new LockingReadException(msg);
    }

    log.debug("Locking Reads, after sql: [{}].", removeEnter(sql));
    return sql;
  }

  @Override
  public int order() {
    return MAX / 2 - 80000;
  }
}
