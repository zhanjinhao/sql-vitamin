package cn.addenda.sql.vitamins.client.common.config;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigDynamicTableName;
import cn.addenda.sql.vitamins.client.common.annotation.DynamicTableName;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.rewriter.dynamic.tablename.DynamicTableNameConfig;
import cn.addenda.sql.vitamins.rewriter.dynamic.tablename.DynamicTableNameConfigBatch;
import cn.addenda.sql.vitamins.rewriter.dynamic.tablename.DynamicTableNameContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2023/6/11 16:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicTableNameConfigUtils {

  public static void configDynamicTableName(Propagation propagation, DynamicTableNameConfigBatch dynamicTableNameConfigBatch) {
    Propagation.assertNotNull(propagation);
    List<DynamicTableNameConfig> dynamicTableNameConfigList = dynamicTableNameConfigBatch.getDynamicTableNameConfigList();
    if (dynamicTableNameConfigList != null) {
      Propagation.configWithPropagation(propagation, dynamicTableNameConfigList,
          DynamicTableNameContext::setDynamicTableNameConfigList,
          DynamicTableNameContext::getDynamicTableNameConfigList);
    }
  }

  public static void configDynamicTableName(ConfigDynamicTableName configDynamicTableName) {
    Propagation propagation = configDynamicTableName.propagation();
    DynamicTableName[] dynamicTableNames = configDynamicTableName.dynamicTableNames();
    List<DynamicTableNameConfig> assemble = assemble(dynamicTableNames);
    configDynamicTableName(propagation, DynamicTableNameConfigBatch.of(assemble));
  }

  private static List<DynamicTableNameConfig> assemble(DynamicTableName[] dynamicTableNames) {
    List<DynamicTableNameConfig> dynamicTableNameConfigList = new ArrayList<>();
    for (DynamicTableName dynamicTableName : dynamicTableNames) {
      DynamicTableNameConfig dynamicTableNameConfig = new DynamicTableNameConfig();
      dynamicTableNameConfig.setOriginalTableName(dynamicTableName.originalTableName());
      dynamicTableNameConfig.setTargetTableName(dynamicTableName.targetTableName());
      dynamicTableNameConfigList.add(dynamicTableNameConfig);
    }
    return dynamicTableNameConfigList;
  }

  public static void pushDynamicTableName(Propagation propagation) {
    Propagation.assertNotNull(propagation);
    // NEW 压入新的
    if (propagation == Propagation.NEW || !DynamicTableNameContext.contextActive()) {
      DynamicTableNameContext.push(new DynamicTableNameConfigBatch());
    }
    // MERGE_* 压入带参数的
    else {
      DynamicTableNameConfigBatch dynamicTableNameConfigBatch = DynamicTableNameContext.peek();
      DynamicTableNameContext.push(new DynamicTableNameConfigBatch(dynamicTableNameConfigBatch));
    }
  }

}
