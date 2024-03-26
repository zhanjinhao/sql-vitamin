package cn.addenda.sql.vitamins.rewriter.dynamicsql;

import cn.addenda.sql.vitamins.rewriter.pojo.Ternary;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.Item;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.*;

import java.util.*;

/**
 * @author addenda
 * @since 2022/11/26 20:56
 */
public class DynamicSQLContext {

  private DynamicSQLContext() {
  }

  public static final String ALL_TABLE = "ALL@ALL";

  private static final ThreadLocal<Stack<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>>>
    CONDITION_TL = ThreadLocal.withInitial(() -> null);
  private static final ThreadLocal<Stack<Map<String, List<Map.Entry<DynamicItemOperation, Item>>>>>
    ITEM_TL = ThreadLocal.withInitial(() -> null);
  private static final ThreadLocal<Stack<DynamicSQLConfig>>
    DYNAMIC_SQL_CONFIG_TL = ThreadLocal.withInitial(() -> null);

  public static void addCondition(DynamicConditionOperation operation, String name, String condition) {
    Stack<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>> conditions = CONDITION_TL.get();
    Map<String, List<Map.Entry<DynamicConditionOperation, String>>> conditionMap = conditions.peek();
    if (name == null) {
      name = DynamicSQLContext.ALL_TABLE;
    }
    List<Map.Entry<DynamicConditionOperation, String>> entries = conditionMap.computeIfAbsent(name, k -> new ArrayList<>());
    ConditionEntry conditionEntry = new ConditionEntry(operation, condition);
    boolean fg = false;
    for (Map.Entry<DynamicConditionOperation, String> entry : entries) {
      if (conditionEntry.equals(entry)) {
        fg = true;
        break;
      }
    }
    if (!fg) {
      entries.add(conditionEntry);
    }
  }

  public static void addItem(DynamicItemOperation operation, String name, String itemName, Object itemValue, boolean dupThenNew) {
    Stack<Map<String, List<Map.Entry<DynamicItemOperation, Item>>>> items = ITEM_TL.get();
    Map<String, List<Map.Entry<DynamicItemOperation, Item>>> itemMap = items.peek();
    if (name == null) {
      name = DynamicSQLContext.ALL_TABLE;
    }
    List<Map.Entry<DynamicItemOperation, Item>> entries = itemMap.computeIfAbsent(name, k -> new ArrayList<>());
    ItemEntry itemEntry = new ItemEntry(operation, new Item(itemName, itemValue));
    Map.Entry<DynamicItemOperation, Item> fg = null;
    for (Map.Entry<DynamicItemOperation, Item> entry : entries) {
      if (itemEntry.equals(entry)) {
        fg = entry;
        break;
      }
    }
    if (fg == null) {
      entries.add(itemEntry);
    } else {
      if (dupThenNew) {
        fg.getValue().setItemValue(itemValue);
      }
    }
  }

  public static void addItem(DynamicItemOperation operation, String name, String itemName, Object itemValue) {
    addItem(operation, name, itemName, itemValue, true);
  }

  public static Map<String, List<Map.Entry<DynamicConditionOperation, String>>> getConditionMap() {
    Stack<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>> conditionMaps = CONDITION_TL.get();
    return conditionMaps.peek();
  }

  public static Map<String, List<Map.Entry<DynamicItemOperation, Item>>> getItemMap() {
    Stack<Map<String, List<Map.Entry<DynamicItemOperation, Item>>>> itemMaps = ITEM_TL.get();
    return itemMaps.peek();
  }

  public static void setJoinUseSubQuery(boolean joinUseSubQuery) {
    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    DynamicSQLConfig dynamicSQLConfig = dynamicSQLConfigs.peek();
    dynamicSQLConfig.setJoinUseSubQuery(joinUseSubQuery);
  }

  public static Boolean getJoinUseSubQuery() {
    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    DynamicSQLConfig dynamicSQLConfig = dynamicSQLConfigs.peek();
    return dynamicSQLConfig.getJoinUseSubQuery();
  }

  public static void setDuplicateKeyUpdate(boolean duplicateKeyUpdate) {
    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    DynamicSQLConfig dynamicSQLConfig = dynamicSQLConfigs.peek();
    dynamicSQLConfig.setDuplicateKeyUpdate(duplicateKeyUpdate);
  }

  public static Boolean getDuplicateKeyUpdate() {
    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    DynamicSQLConfig dynamicSQLConfig = dynamicSQLConfigs.peek();
    return dynamicSQLConfig.getDuplicateKeyUpdate();
  }

  public static void setInsertSelectAddItemMode(InsertAddSelectItemMode insertAddSelectItemMode) {
    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    DynamicSQLConfig dynamicSQLConfig = dynamicSQLConfigs.peek();
    dynamicSQLConfig.setInsertAddSelectItemMode(insertAddSelectItemMode);
  }

  public static InsertAddSelectItemMode getInsertSelectAddItemMode() {
    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    DynamicSQLConfig dynamicSQLConfig = dynamicSQLConfigs.peek();
    return dynamicSQLConfig.getInsertAddSelectItemMode();
  }

  public static void setUpdateItemMode(UpdateItemMode updateItemMode) {
    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    DynamicSQLConfig dynamicSQLConfig = dynamicSQLConfigs.peek();
    dynamicSQLConfig.setUpdateItemMode(updateItemMode);
  }

  public static UpdateItemMode getUpdateItemMode() {
    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    DynamicSQLConfig dynamicSQLConfig = dynamicSQLConfigs.peek();
    return dynamicSQLConfig.getUpdateItemMode();
  }

  public static void push() {
    push(new Ternary<>(new HashMap<>(), new HashMap<>(), new DynamicSQLConfig()));
  }

  public static void push(Ternary<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>,
    Map<String, List<Map.Entry<DynamicItemOperation, Item>>>, DynamicSQLConfig> ternary) {
    Stack<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>> conditions = CONDITION_TL.get();
    if (conditions == null) {
      conditions = new Stack<>();
      CONDITION_TL.set(conditions);
    }
    conditions.push(ternary.getF1());

    Stack<Map<String, List<Map.Entry<DynamicItemOperation, Item>>>> items = ITEM_TL.get();
    if (items == null) {
      items = new Stack<>();
      ITEM_TL.set(items);
    }
    items.push(ternary.getF2());

    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    if (dynamicSQLConfigs == null) {
      dynamicSQLConfigs = new Stack<>();
      DYNAMIC_SQL_CONFIG_TL.set(dynamicSQLConfigs);
    }
    dynamicSQLConfigs.push(ternary.getF3());
  }

  public static void pop() {
    Stack<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>> conditions = CONDITION_TL.get();
    conditions.pop();
    if (conditions.isEmpty()) {
      CONDITION_TL.remove();
    }

    Stack<Map<String, List<Map.Entry<DynamicItemOperation, Item>>>> items = ITEM_TL.get();
    items.pop();
    if (items.isEmpty()) {
      ITEM_TL.remove();
    }

    Stack<DynamicSQLConfig> dynamicSQLConfigs = DYNAMIC_SQL_CONFIG_TL.get();
    dynamicSQLConfigs.pop();
    if (dynamicSQLConfigs.isEmpty()) {
      DYNAMIC_SQL_CONFIG_TL.remove();
    }
  }

  public static Ternary<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>,
    Map<String, List<Map.Entry<DynamicItemOperation, Item>>>, DynamicSQLConfig> peek() {
    Ternary<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>,
      Map<String, List<Map.Entry<DynamicItemOperation, Item>>>, DynamicSQLConfig> ternary = new Ternary<>();
    ternary.setF1(CONDITION_TL.get().peek());
    ternary.setF2(ITEM_TL.get().peek());
    ternary.setF3(DYNAMIC_SQL_CONFIG_TL.get().peek());
    return ternary;
  }

  public static boolean contextActive() {
    return CONDITION_TL.get() != null && ITEM_TL.get() != null && DYNAMIC_SQL_CONFIG_TL.get() != null;
  }

  private static class ConditionEntry implements Map.Entry<DynamicConditionOperation, String> {
    private final DynamicConditionOperation operation;
    private String condition;

    public ConditionEntry(DynamicConditionOperation operation, String condition) {
      this.operation = operation;
      this.condition = condition;
    }

    @Override
    public DynamicConditionOperation getKey() {
      return operation;
    }

    @Override
    public String getValue() {
      return condition;
    }

    @Override
    public String setValue(String newValue) {
      String oldValue = condition;
      condition = newValue;
      return oldValue;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ConditionEntry)) return false;
      ConditionEntry that = (ConditionEntry) o;
      return operation == that.operation && Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
      return Objects.hash(operation, condition);
    }
  }

  private static class ItemEntry implements Map.Entry<DynamicItemOperation, Item> {
    private final DynamicItemOperation operation;
    private Item item;

    public ItemEntry(DynamicItemOperation operation, Item item) {
      this.operation = operation;
      this.item = item;
    }

    @Override
    public DynamicItemOperation getKey() {
      return operation;
    }

    @Override
    public Item getValue() {
      return item;
    }

    @Override
    public Item setValue(Item newValue) {
      Item oldValue = item;
      item = newValue;
      return oldValue;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ItemEntry)) return false;
      ItemEntry itemEntry = (ItemEntry) o;
      return operation == itemEntry.operation && Objects.equals(item, itemEntry.item);
    }

    @Override
    public int hashCode() {
      return Objects.hash(operation, item);
    }
  }

  @Setter
  @Getter
  @ToString
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DynamicSQLConfig {
    private Boolean joinUseSubQuery;
    private Boolean duplicateKeyUpdate;
    private InsertAddSelectItemMode insertAddSelectItemMode;
    private UpdateItemMode updateItemMode;

    public DynamicSQLConfig(DynamicSQLConfig dynamicSQLConfig) {
      this.joinUseSubQuery = dynamicSQLConfig.joinUseSubQuery;
      this.duplicateKeyUpdate = dynamicSQLConfig.duplicateKeyUpdate;
      this.insertAddSelectItemMode = dynamicSQLConfig.insertAddSelectItemMode;
      this.updateItemMode = dynamicSQLConfig.updateItemMode;
    }
  }

}
