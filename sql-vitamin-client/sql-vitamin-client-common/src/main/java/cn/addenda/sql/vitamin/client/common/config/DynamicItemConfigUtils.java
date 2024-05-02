package cn.addenda.sql.vitamin.client.common.config;

import cn.addenda.sql.vitamin.client.common.annotation.ConfigDynamicItem;
import cn.addenda.sql.vitamin.client.common.annotation.DynamicItem;
import cn.addenda.sql.vitamin.client.common.constant.Propagation;
import cn.addenda.sql.vitamin.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.dynamic.item.DynamicItemConfig;
import cn.addenda.sql.vitamin.rewriter.dynamic.item.DynamicItemConfigBatch;
import cn.addenda.sql.vitamin.rewriter.dynamic.item.DynamicItemContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2023/6/11 16:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicItemConfigUtils {

  public static void configDynamicItem(Propagation propagation, DynamicItemConfigBatch dynamicItemConfigBatch) {
    Propagation.assertNotNull(propagation);
    List<DynamicItemConfig> dynamicItemConfigList = dynamicItemConfigBatch.getDynamicItemConfigList();
    if (dynamicItemConfigList != null) {
      Propagation.configWithPropagation(propagation, dynamicItemConfigList,
          DynamicItemContext::setDynamicItemConfigList,
          DynamicItemContext::getDynamicItemConfigList);
    }
  }

  public static void configDynamicItem(ConfigDynamicItem configDynamicItem, DataConvertorRegistry dataConvertorRegistry) {
    Propagation propagation = configDynamicItem.propagation();
    DynamicItem[] dynamicItems = configDynamicItem.dynamicItems();
    List<DynamicItemConfig> assemble = assemble(dynamicItems, dataConvertorRegistry);
    configDynamicItem(propagation, DynamicItemConfigBatch.of(assemble));
  }

  private static List<DynamicItemConfig> assemble(DynamicItem[] dynamicItems, DataConvertorRegistry dataConvertorRegistry) {
    List<DynamicItemConfig> dynamicItemConfigList = new ArrayList<>();
    for (DynamicItem dynamicItem : dynamicItems) {
      DynamicItemConfig dynamicItemConfig = new DynamicItemConfig();
      dynamicItemConfig.setDynamicItemOperation(dynamicItem.dynamicItemOperation());
      dynamicItemConfig.setTableName(dynamicItem.tableName());
      dynamicItemConfig.setItemName(dynamicItem.itemName());
      dynamicItemConfig.setItemValue(dataConvertorRegistry.parse(dynamicItem.itemValue(), dynamicItem.itemValueClass()));
      dynamicItemConfig.setInsertAddSelectItemMode(dynamicItem.insertAddSelectItemMode());
      dynamicItemConfig.setDuplicateKeyUpdate(dynamicItem.duplicateKeyUpdate());
      dynamicItemConfig.setUpdateItemMode(dynamicItem.updateItemMode());
      dynamicItemConfigList.add(dynamicItemConfig);
    }
    return dynamicItemConfigList;
  }

  public static void pushDynamicItem(Propagation propagation) {
    Propagation.assertNotNull(propagation);
    // NEW 压入新的
    if (propagation == Propagation.NEW || !DynamicItemContext.contextActive()) {
      DynamicItemContext.push(new DynamicItemConfigBatch());
    }
    // MERGE_* 压入带参数的
    else {
      DynamicItemConfigBatch dynamicItemConfigBatch = DynamicItemContext.peek();
      DynamicItemContext.push(new DynamicItemConfigBatch(dynamicItemConfigBatch));
    }
  }

}
