package cn.addenda.sql.vitamin.client.common.config;

import cn.addenda.sql.vitamin.client.common.annotation.ConfigDynamicCondition;
import cn.addenda.sql.vitamin.client.common.annotation.DynamicCondition;
import cn.addenda.sql.vitamin.client.common.constant.Propagation;
import cn.addenda.sql.vitamin.rewriter.dynamic.condition.DynamicConditionConfig;
import cn.addenda.sql.vitamin.rewriter.dynamic.condition.DynamicConditionConfigBatch;
import cn.addenda.sql.vitamin.rewriter.dynamic.condition.DynamicConditionContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2023/6/11 16:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicConditionConfigUtils {

  public static void configDynamicCondition(
          Propagation propagation, DynamicConditionConfigBatch dynamicConditionConfigBatch) {
    Propagation.assertNotNull(propagation);
    List<DynamicConditionConfig> dynamicConditionConfigList = dynamicConditionConfigBatch.getDynamicConditionConfigList();
    if (dynamicConditionConfigList != null) {
      Propagation.configWithPropagation(propagation, dynamicConditionConfigList,
          DynamicConditionContext::setDynamicConditionConfigList,
          DynamicConditionContext::getDynamicConditionConfigList);
    }
  }

  public static void configDynamicCondition(ConfigDynamicCondition configDynamicCondition) {
    Propagation propagation = configDynamicCondition.propagation();
    DynamicCondition[] dynamicConditions = configDynamicCondition.dynamicConditions();
    List<DynamicConditionConfig> assemble = assemble(dynamicConditions);
    configDynamicCondition(propagation, DynamicConditionConfigBatch.of(assemble));
  }

  private static List<DynamicConditionConfig> assemble(DynamicCondition[] dynamicConditions) {
    List<DynamicConditionConfig> dynamicConditionConfigList = new ArrayList<>();
    for (DynamicCondition dynamicCondition : dynamicConditions) {
      DynamicConditionConfig dynamicConditionConfig = new DynamicConditionConfig();
      dynamicConditionConfig.setDynamicConditionOperation(dynamicCondition.dynamicConditionOperation());
      dynamicConditionConfig.setTableOrViewName(dynamicCondition.tableOrViewName());
      dynamicConditionConfig.setCondition(dynamicCondition.condition());
      dynamicConditionConfig.setJoinUseSubQuery(dynamicCondition.joinUseSubQuery());
      dynamicConditionConfigList.add(dynamicConditionConfig);
    }
    return dynamicConditionConfigList;
  }

  public static void pushDynamicCondition(Propagation propagation) {
    Propagation.assertNotNull(propagation);
    // NEW 压入新的
    if (propagation == Propagation.NEW || !DynamicConditionContext.contextActive()) {
      DynamicConditionContext.push(new DynamicConditionConfigBatch());
    }
    // MERGE_* 压入带参数的
    else {
      DynamicConditionConfigBatch dynamicConditionConfigBatch = DynamicConditionContext.peek();
      DynamicConditionContext.push(new DynamicConditionConfigBatch(dynamicConditionConfigBatch));
    }
  }

}
