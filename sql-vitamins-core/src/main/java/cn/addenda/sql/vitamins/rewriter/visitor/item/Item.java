package cn.addenda.sql.vitamins.rewriter.visitor.item;

import lombok.*;

import java.util.Objects;

/**
 * @author addenda
 * @since 2023/5/12 19:57
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {

  private String itemName;

  private Object itemValue;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Item item = (Item) o;
    return Objects.equals(itemName, item.itemName) && Objects.equals(itemValue, item.itemValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemName, itemValue);
  }
}
