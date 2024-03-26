package cn.addenda.sql.vitamins.rewriter.pojo;

import lombok.*;

import java.util.Map;
import java.util.Objects;

/**
 * 二元
 *
 * @author addenda
 * @since 2023/1/21 16:00
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Binary<T1, T2> implements Map.Entry<T1, T2> {

  private T1 f1;

  private T2 f2;

  @Override
  public T1 getKey() {
    return f1;
  }

  @Override
  public T2 getValue() {
    return f2;
  }

  @Override
  public T2 setValue(T2 value) {
    T2 t2 = this.f2;
    this.f2 = value;
    return t2;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Binary)) return false;
    Binary<?, ?> binary = (Binary<?, ?>) o;
    return Objects.equals(f1, binary.f1) && Objects.equals(f2, binary.f2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(f1, f2);
  }

}
