package cn.addenda.sql.vitamins.client.common.constant;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/6/10 20:37
 */
public enum Propagation {

  /**
   * 新开一个管理器
   */
  NEW,
  /**
   * 新的参数与旧的参数合并，如果新旧都存在某个参数以新的为主
   */
  MERGE_NEW,
  /**
   * 新的参数与旧的参数合并，如果新旧都存在某个参数以旧的为主
   */
  MERGE_OLD;

  public static <T> void configWithPropagation(
    Propagation propagation, T obj, Consumer<T> consumer, Supplier<T> supplier) {
    if (propagation == Propagation.NEW || propagation == Propagation.MERGE_NEW) {
      consumer.accept(obj);
    } else {
      T t = supplier.get();
      if (t == null) {
        consumer.accept(obj);
      }
    }
  }

}
