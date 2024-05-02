package cn.addenda.sql.vitamin.rewriter.dynamic.tablename;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * @author addenda
 * @since 2022/11/26 20:56
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicTableNameContext {

  private static final ThreadLocal<Deque<DynamicTableNameConfigBatch>> TABLE_NAME_TL = ThreadLocal.withInitial(() -> null);

  public static boolean contextActive() {
    return TABLE_NAME_TL.get() != null;
  }

  public static DynamicTableNameConfigBatch peek() {
    return TABLE_NAME_TL.get().peek();
  }

  public static void push(DynamicTableNameConfigBatch dynamicTableNameConfigBatch) {
    Deque<DynamicTableNameConfigBatch> dynamicTableNameConfigBatchDeque = TABLE_NAME_TL.get();
    if (dynamicTableNameConfigBatchDeque == null) {
      dynamicTableNameConfigBatchDeque = new ArrayDeque<>();
      TABLE_NAME_TL.set(dynamicTableNameConfigBatchDeque);
    }
    dynamicTableNameConfigBatchDeque.push(dynamicTableNameConfigBatch);
  }

  public static void setDynamicTableNameConfigList(List<DynamicTableNameConfig> dynamicTableNameConfigList) {
    Deque<DynamicTableNameConfigBatch> dynamicTableNameConfigBatchDeque = TABLE_NAME_TL.get();
    DynamicTableNameConfigBatch conditionConfigBatch = dynamicTableNameConfigBatchDeque.peek();
    conditionConfigBatch.setDynamicTableNameConfigList(dynamicTableNameConfigList);
  }

  public static List<DynamicTableNameConfig> getDynamicTableNameConfigList() {
    Deque<DynamicTableNameConfigBatch> dynamicTableNameConfigBatchDeque = TABLE_NAME_TL.get();
    DynamicTableNameConfigBatch dynamicTableNameConfigBatch = dynamicTableNameConfigBatchDeque.peek();
    return dynamicTableNameConfigBatch.getDynamicTableNameConfigList();
  }

  public static DynamicTableNameConfigBatch pop() {
    Deque<DynamicTableNameConfigBatch> dynamicTableNameConfigBatchDeque = TABLE_NAME_TL.get();
    DynamicTableNameConfigBatch pop = dynamicTableNameConfigBatchDeque.pop();
    if (dynamicTableNameConfigBatchDeque.isEmpty()) {
      TABLE_NAME_TL.remove();
    }
    return pop;
  }

  public static void remove() {
    TABLE_NAME_TL.remove();
  }

}
