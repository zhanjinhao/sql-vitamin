package cn.addenda.sql.vitamins.rewriter.dynamic.condition;

import lombok.*;

import java.util.*;

/**
 * @author addenda
 * @since 2022/11/26 20:56
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicConditionContext {

  public static final String ALL_TABLE = "ALL@ALL";

  private static final ThreadLocal<Deque<DynamicConditionConfigBatch>> CONDITION_TL = ThreadLocal.withInitial(() -> null);

  public static boolean contextActive() {
    return CONDITION_TL.get() != null;
  }

  public static DynamicConditionConfigBatch peek() {
    return CONDITION_TL.get().peek();
  }

  public static DynamicConditionConfigBatch pop() {
    Deque<DynamicConditionConfigBatch> dynamicConditionConfigBatchDeque = CONDITION_TL.get();
    DynamicConditionConfigBatch pop = CONDITION_TL.get().pop();
    if (dynamicConditionConfigBatchDeque.isEmpty()) {
      CONDITION_TL.remove();
    }
    return pop;
  }

  public static void remove() {
    CONDITION_TL.remove();
  }

  public static void push(DynamicConditionConfigBatch dynamicConditionConfigBatch) {
    Deque<DynamicConditionConfigBatch> dynamicConditionConfigBatchDeque = CONDITION_TL.get();
    if (dynamicConditionConfigBatchDeque == null) {
      dynamicConditionConfigBatchDeque = new ArrayDeque<>();
      CONDITION_TL.set(dynamicConditionConfigBatchDeque);
    }
    dynamicConditionConfigBatchDeque.push(dynamicConditionConfigBatch);
  }

  public static void setDynamicConditionConfigList(List<DynamicConditionConfig> dynamicConditionConfigList) {
    Deque<DynamicConditionConfigBatch> dynamicConditionConfigBatchDeque = CONDITION_TL.get();
    DynamicConditionConfigBatch dynamicConditionConfigBatch = dynamicConditionConfigBatchDeque.peek();
    dynamicConditionConfigBatch.setDynamicConditionConfigList(dynamicConditionConfigList);
  }

  public static List<DynamicConditionConfig> getDynamicConditionConfigList() {
    Deque<DynamicConditionConfigBatch> dynamicConditionConfigBatchDeque = CONDITION_TL.get();
    DynamicConditionConfigBatch dynamicConditionConfigBatch = dynamicConditionConfigBatchDeque.peek();
    return dynamicConditionConfigBatch.getDynamicConditionConfigList();
  }

}
