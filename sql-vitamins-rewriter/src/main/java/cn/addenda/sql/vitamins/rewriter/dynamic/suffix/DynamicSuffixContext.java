package cn.addenda.sql.vitamins.rewriter.dynamic.suffix;

import lombok.*;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author addenda
 * @since 2023/4/27 20:21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicSuffixContext {

  private static final String W_LOCK = " for update ";
  private static final String R_LOCK = " lock in share mode ";
  private static final String LIMIT = " limit %d ";
  private static final String ORDER_BY = " order by %s ";

  private static final ThreadLocal<Deque<DynamicSuffixConfig>> SUFFIX_TL = ThreadLocal.withInitial(() -> null);

  public static void setSuffix(String suffix) {
    Deque<DynamicSuffixConfig> dynamicSuffixConfigDeque = SUFFIX_TL.get();
    DynamicSuffixConfig peek = dynamicSuffixConfigDeque.peek();
    peek.setSuffix(suffix);
  }

  public static String getSuffix() {
    Deque<DynamicSuffixConfig> dynamicSuffixConfigDeque = SUFFIX_TL.get();
    DynamicSuffixConfig peek = dynamicSuffixConfigDeque.peek();
    return peek.getSuffix();
  }

  public static void push(DynamicSuffixConfig dynamicSuffixConfig) {
    Deque<DynamicSuffixConfig> suffixDeque = SUFFIX_TL.get();
    if (suffixDeque == null) {
      suffixDeque = new ArrayDeque<>();
      SUFFIX_TL.set(suffixDeque);
    }
    suffixDeque.push(dynamicSuffixConfig);
  }

  public static DynamicSuffixConfig pop() {
    Deque<DynamicSuffixConfig> dynamicSuffixConfigDeque = SUFFIX_TL.get();
    DynamicSuffixConfig pop = dynamicSuffixConfigDeque.pop();
    if (dynamicSuffixConfigDeque.isEmpty()) {
      SUFFIX_TL.remove();
    }
    return pop;
  }

  public static String getRLock() {
    return R_LOCK;
  }

  public static String getWLock() {
    return W_LOCK;
  }

  public static String getLimit(int limit) {
    return String.format(LIMIT, limit);
  }

  public static String getOrderBy(String orderBy) {
    return String.format(ORDER_BY, orderBy);
  }

  public static void remove() {
    SUFFIX_TL.remove();
  }

  public static DynamicSuffixConfig peek() {
    return SUFFIX_TL.get().peek();
  }

  public static boolean contextActive() {
    return SUFFIX_TL.get() != null;
  }

}
