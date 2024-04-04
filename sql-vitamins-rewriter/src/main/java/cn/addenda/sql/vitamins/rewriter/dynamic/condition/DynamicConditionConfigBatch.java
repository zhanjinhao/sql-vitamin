package cn.addenda.sql.vitamins.rewriter.dynamic.condition;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DynamicConditionConfigBatch {

  private List<DynamicConditionConfig> dynamicConditionConfigList;

  public DynamicConditionConfigBatch(DynamicConditionConfigBatch dynamicConditionConfigBatch) {
    dynamicConditionConfigList = new ArrayList<>();
    dynamicConditionConfigList.addAll(dynamicConditionConfigBatch.getDynamicConditionConfigList());
  }

  public static DynamicConditionConfigBatch of(List<DynamicConditionConfig> dynamicConditionConfigList) {
    return new DynamicConditionConfigBatch(dynamicConditionConfigList);
  }

}
