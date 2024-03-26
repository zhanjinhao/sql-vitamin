package cn.addenda.sql.vitamins.rewriter.tombstone;

import lombok.*;

import java.util.Stack;

/**
 * @author addenda
 * @since 2023/5/17 22:42
 */
public class TombstoneContext {

  private TombstoneContext() {
  }

  private static final ThreadLocal<Stack<TombstoneConfig>> TOMBSTONE_CONTEXT_TL = ThreadLocal.withInitial(() -> null);

  public static void setJoinUseSubQuery(boolean joinUseSubQuery) {
    Stack<TombstoneConfig> tombstoneConfigs = TOMBSTONE_CONTEXT_TL.get();
    TombstoneConfig tombstoneConfig = tombstoneConfigs.peek();
    tombstoneConfig.setJoinUseSubQuery(joinUseSubQuery);
  }

  public static Boolean getJoinUseSubQuery() {
    Stack<TombstoneConfig> tombstoneConfigs = TOMBSTONE_CONTEXT_TL.get();
    TombstoneConfig tombstoneConfig = tombstoneConfigs.peek();
    return tombstoneConfig.getJoinUseSubQuery();
  }

  public static void setDisable(boolean disable) {
    Stack<TombstoneConfig> tombstoneConfigs = TOMBSTONE_CONTEXT_TL.get();
    TombstoneConfig tombstoneConfig = tombstoneConfigs.peek();
    tombstoneConfig.setDisable(disable);
  }

  public static Boolean getDisable() {
    Stack<TombstoneConfig> tombstoneConfigs = TOMBSTONE_CONTEXT_TL.get();
    TombstoneConfig tombstoneConfig = tombstoneConfigs.peek();
    return tombstoneConfig.getDisable();
  }

  public static void push() {
    push(new TombstoneConfig());
  }

  public static void push(TombstoneConfig tombstoneConfig) {
    Stack<TombstoneConfig> tombstoneConfigs = TOMBSTONE_CONTEXT_TL.get();
    if (tombstoneConfigs == null) {
      tombstoneConfigs = new Stack<>();
      TOMBSTONE_CONTEXT_TL.set(tombstoneConfigs);
    }
    tombstoneConfigs.push(tombstoneConfig);
  }

  public static void pop() {
    Stack<TombstoneConfig> tombstoneConfigs = TOMBSTONE_CONTEXT_TL.get();
    tombstoneConfigs.pop();
    if (tombstoneConfigs.isEmpty()) {
      TOMBSTONE_CONTEXT_TL.remove();
    }
  }

  public static boolean contextActive() {
    return TOMBSTONE_CONTEXT_TL.get() != null;
  }

  public static TombstoneConfig peek() {
    return TOMBSTONE_CONTEXT_TL.get().peek();
  }

  @Setter
  @Getter
  @ToString
  @AllArgsConstructor
  @NoArgsConstructor
  public static class TombstoneConfig {
    private Boolean disable;

    private Boolean joinUseSubQuery;

    public TombstoneConfig(TombstoneConfig tombstoneConfig) {
      this.disable = tombstoneConfig.disable;
      this.joinUseSubQuery = tombstoneConfig.joinUseSubQuery;
    }
  }

}
