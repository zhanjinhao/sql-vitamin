package cn.addenda.sql.vitamins.rewriter.dynamic.condition;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DynamicConditionConfig {

  private DynamicConditionOperation dynamicConditionOperation;

  private String tableOrViewName;

  private String condition;

  private Boolean joinUseSubQuery;

  public DynamicConditionConfig(DynamicConditionConfig dynamicConditionConfig) {
    this.dynamicConditionOperation = dynamicConditionConfig.dynamicConditionOperation;
    this.tableOrViewName = dynamicConditionConfig.tableOrViewName;
    this.condition = dynamicConditionConfig.condition;
    this.joinUseSubQuery = dynamicConditionConfig.joinUseSubQuery;
  }

  public static DynamicConditionConfig of(
      DynamicConditionOperation dynamicConditionOperation, String tableOrViewName, String condition, Boolean joinUseSubQuery) {
    return new DynamicConditionConfig(dynamicConditionOperation, tableOrViewName, condition, joinUseSubQuery);
  }

}