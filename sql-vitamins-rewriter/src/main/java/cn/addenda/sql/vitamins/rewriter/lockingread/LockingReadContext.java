package cn.addenda.sql.vitamins.rewriter.lockingread;

import lombok.*;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author addenda
 * @since 2023/4/27 20:21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LockingReadContext {

  public static final String W_LOCK = "W";
  public static final String R_LOCK = "R";

  private static final ThreadLocal<Deque<LockingReadConfig>> LOCK_TL = ThreadLocal.withInitial(() -> null);

  public static void setLock(String lock) {
    if (!W_LOCK.equals(lock) && !R_LOCK.equals(lock)) {
      throw new LockingReadException("不支持的LOCK类型，当前LOCK类型：" + lock + "。");
    }
    Deque<LockingReadConfig> lockingReadConfigDeque = LOCK_TL.get();
    LockingReadConfig peek = lockingReadConfigDeque.peek();
    peek.setLock(lock);
  }

  public static String getLock() {
    Deque<LockingReadConfig> lockingReadConfigDeque = LOCK_TL.get();
    LockingReadConfig peek = lockingReadConfigDeque.peek();
    return peek.getLock();
  }

  public static void push(LockingReadConfig lockingReadConfig) {
    Deque<LockingReadConfig> locks = LOCK_TL.get();
    if (locks == null) {
      locks = new ArrayDeque<>();
      LOCK_TL.set(locks);
    }
    locks.push(lockingReadConfig);
  }

  public static LockingReadConfig pop() {
    Deque<LockingReadConfig> lockingReadConfigDeque = LOCK_TL.get();
    LockingReadConfig pop = lockingReadConfigDeque.pop();
    if (lockingReadConfigDeque.isEmpty()) {
      LOCK_TL.remove();
    }
    return pop;
  }

  public static void remove() {
    LOCK_TL.remove();
  }

  public static LockingReadConfig peek() {
    return LOCK_TL.get().peek();
  }

  public static boolean contextActive() {
    return LOCK_TL.get() != null;
  }

}
