package cn.addenda.sql.vitamins.rewriter.tombstone;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author addenda
 * @since 2023/5/17 22:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TombstoneContext {

  private static final ThreadLocal<Deque<TombstoneConfig>> CONTEXT_TL = ThreadLocal.withInitial(() -> null);

  public static void setJoinUseSubQuery(boolean joinUseSubQuery) {
    Deque<TombstoneConfig> tombstoneConfigDeque = CONTEXT_TL.get();
    TombstoneConfig tombstoneConfig = tombstoneConfigDeque.peek();
    tombstoneConfig.setJoinUseSubQuery(joinUseSubQuery);
  }

  public static Boolean getJoinUseSubQuery() {
    Deque<TombstoneConfig> tombstoneConfigDeque = CONTEXT_TL.get();
    TombstoneConfig tombstoneConfig = tombstoneConfigDeque.peek();
    return tombstoneConfig.getJoinUseSubQuery();
  }

  public static void setDisable(boolean disable) {
    Deque<TombstoneConfig> tombstoneConfigDeque = CONTEXT_TL.get();
    TombstoneConfig tombstoneConfig = tombstoneConfigDeque.peek();
    tombstoneConfig.setDisable(disable);
  }

  public static Boolean getDisable() {
    Deque<TombstoneConfig> tombstoneConfigDeque = CONTEXT_TL.get();
    TombstoneConfig tombstoneConfig = tombstoneConfigDeque.peek();
    return tombstoneConfig.getDisable();
  }

  public static void push(TombstoneConfig tombstoneConfig) {
    Deque<TombstoneConfig> tombstoneConfigDeque = CONTEXT_TL.get();
    if (tombstoneConfigDeque == null) {
      tombstoneConfigDeque = new ArrayDeque<>();
      CONTEXT_TL.set(tombstoneConfigDeque);
    }
    tombstoneConfigDeque.push(tombstoneConfig);
  }

  public static TombstoneConfig pop() {
    Deque<TombstoneConfig> tombstoneConfigDeque = CONTEXT_TL.get();
    TombstoneConfig pop = tombstoneConfigDeque.pop();
    if (tombstoneConfigDeque.isEmpty()) {
      CONTEXT_TL.remove();
    }
    return pop;
  }

  public static boolean contextActive() {
    return CONTEXT_TL.get() != null;
  }

  public static TombstoneConfig peek() {
    return CONTEXT_TL.get().peek();
  }

  public static void remove() {
    CONTEXT_TL.remove();
  }

}
