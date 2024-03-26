package cn.addenda.sql.vitamins.rewriter.lockingreads;

import lombok.*;

import java.util.Stack;

/**
 * @author addenda
 * @since 2023/4/27 20:21
 */
public class LockingReadsContext {

  private LockingReadsContext() {
  }

  public static final String W_LOCK = "W";
  public static final String R_LOCK = "R";

  private static final ThreadLocal<Stack<LockingReadsConfig>> LOCK_TL = ThreadLocal.withInitial(() -> null);

  public static void setLock(String lock) {
    if (!W_LOCK.equals(lock) && !R_LOCK.equals(lock)) {
      throw new LockingReadsException("不支持的LOCK类型，当前LOCK类型：" + lock + "。");
    }
    Stack<LockingReadsConfig> lockingReadsConfigs = LOCK_TL.get();
    LockingReadsConfig peek = lockingReadsConfigs.peek();
    peek.setLock(lock);
  }

  public static String getLock() {
    Stack<LockingReadsConfig> lockingReadsConfigs = LOCK_TL.get();
    LockingReadsConfig peek = lockingReadsConfigs.peek();
    return peek.getLock();
  }

  public static void push() {
    push(new LockingReadsConfig());
  }

  public static void push(LockingReadsConfig lockingReadsConfig) {
    Stack<LockingReadsConfig> locks = LOCK_TL.get();
    if (locks == null) {
      locks = new Stack<>();
      LOCK_TL.set(locks);
    }
    locks.push(lockingReadsConfig);
  }

  public static void pop() {
    Stack<LockingReadsConfig> lockingReadsConfigs = LOCK_TL.get();
    lockingReadsConfigs.pop();
    if (lockingReadsConfigs.isEmpty()) {
      LOCK_TL.remove();
    }
  }

  public static LockingReadsConfig peek() {
    return LOCK_TL.get().peek();
  }

  public static boolean contextActive() {
    return LOCK_TL.get() != null;
  }

  @Setter
  @Getter
  @ToString
  @AllArgsConstructor
  @NoArgsConstructor
  public static class LockingReadsConfig {
    private String lock;

    public LockingReadsConfig(LockingReadsConfig lockingReadsConfig) {
      this.lock = lockingReadsConfig.lock;
    }
  }

}
