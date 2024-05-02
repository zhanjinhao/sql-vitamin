package cn.addenda.sql.vitamin.rewriter.dynamic.item;

import lombok.*;

import java.util.*;

/**
 * @author addenda
 * @since 2022/11/26 20:56
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicItemContext {

  public static final String ALL_TABLE = "ALL@ALL";

  private static final ThreadLocal<Deque<DynamicItemConfigBatch>> ITEM_TL = ThreadLocal.withInitial(() -> null);

  public static boolean contextActive() {
    return ITEM_TL.get() != null;
  }

  public static DynamicItemConfigBatch peek() {
    return ITEM_TL.get().peek();
  }

  public static void push(DynamicItemConfigBatch dynamicItemConfigBatch) {
    Deque<DynamicItemConfigBatch> dynamicItemConfigBatchDeque = ITEM_TL.get();
    if (dynamicItemConfigBatchDeque == null) {
      dynamicItemConfigBatchDeque = new ArrayDeque<>();
      ITEM_TL.set(dynamicItemConfigBatchDeque);
    }
    dynamicItemConfigBatchDeque.push(dynamicItemConfigBatch);
  }

  public static void setDynamicItemConfigList(List<DynamicItemConfig> dynamicItemConfigList) {
    Deque<DynamicItemConfigBatch> dynamicItemConfigBatchDeque = ITEM_TL.get();
    DynamicItemConfigBatch conditionConfigBatch = dynamicItemConfigBatchDeque.peek();
    conditionConfigBatch.setDynamicItemConfigList(dynamicItemConfigList);
  }

  public static List<DynamicItemConfig> getDynamicItemConfigList() {
    Deque<DynamicItemConfigBatch> dynamicItemConfigBatchDeque = ITEM_TL.get();
    DynamicItemConfigBatch dynamicItemConfigBatch = dynamicItemConfigBatchDeque.peek();
    return dynamicItemConfigBatch.getDynamicItemConfigList();
  }

  public static DynamicItemConfigBatch pop() {
    Deque<DynamicItemConfigBatch> dynamicItemConfigBatchDeque = ITEM_TL.get();
    DynamicItemConfigBatch pop = dynamicItemConfigBatchDeque.pop();
    if (dynamicItemConfigBatchDeque.isEmpty()) {
      ITEM_TL.remove();
    }
    return pop;
  }

  public static void remove() {
    ITEM_TL.remove();
  }

}
