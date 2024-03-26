package cn.addenda.sql.vitamins.rewriter.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2023/6/5 21:27
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterableUtils {

  private static final int BATCH_SIZE = 100;

  /**
   * 集合做拆分
   */
  public static <T> List<List<T>> split(Iterable<T> iterable, int quantity) {
    if (iterable == null) {
      return new ArrayList<>();
    }
    List<List<T>> listList = new ArrayList<>();
    List<T> seg = null;
    int i = 0;
    for (T t : iterable) {
      if (i % quantity == 0) {
        if (seg != null) {
          listList.add(seg);
        }
        seg = new ArrayList<>();
      }
      seg.add(t);
      i++;
    }
    if (seg != null && !seg.isEmpty()) {
      listList.add(seg);
    }
    return listList;
  }

  public static <T> List<T> merge(Iterable<T> a, Iterable<T> b) {
    if (a == null && b == null) {
      return new ArrayList<>();
    }
    if (a == null) {
      return toList(b);
    }
    if (b == null) {
      return toList(a);
    }
    List<T> result = new ArrayList<>();
    a.forEach(result::add);
    b.forEach(result::add);
    return result;
  }

  public static <T> List<T> toList(Iterable<T> iterable) {
    List<T> list = new ArrayList<>();
    iterable.forEach(list::add);
    return list;
  }

}
