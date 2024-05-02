package cn.addenda.sql.vitamin.rewriter.dynamic.item;

import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DynamicItemConfig {

  private DynamicItemOperation dynamicItemOperation;
  private String tableName;
  private String itemName;
  private Object itemValue;
  private InsertAddSelectItemMode insertAddSelectItemMode;
  private Boolean duplicateKeyUpdate;
  private UpdateItemMode updateItemMode;

  public DynamicItemConfig(DynamicItemConfig dynamicItemConfig) {
    this.dynamicItemOperation = dynamicItemConfig.dynamicItemOperation;
    this.tableName = dynamicItemConfig.tableName;
    this.itemName = dynamicItemConfig.itemName;
    this.itemValue = dynamicItemConfig.itemValue;
    this.insertAddSelectItemMode = dynamicItemConfig.insertAddSelectItemMode;
    this.duplicateKeyUpdate = dynamicItemConfig.duplicateKeyUpdate;
    this.updateItemMode = dynamicItemConfig.updateItemMode;
  }

  public static DynamicItemConfig of(
      DynamicItemOperation dynamicItemOperation, String tableName,
      String itemName, Object itemValue,
      InsertAddSelectItemMode insertAddSelectItemMode, Boolean duplicateKeyUpdate, UpdateItemMode updateItemMode) {
    return new DynamicItemConfig(
        dynamicItemOperation, tableName,
        itemName, itemValue,
        insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
  }

  public static DynamicItemConfig of(
      DynamicItemOperation dynamicItemOperation, String tableName,
      String itemName, Object itemValue) {
    return new DynamicItemConfig(dynamicItemOperation, tableName,
        itemName, itemValue, null, null, null);
  }

}