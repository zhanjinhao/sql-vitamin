package cn.addenda.sql.vitamin.rewriter.dynamic.item;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DynamicItemConfigBatch {

  private List<DynamicItemConfig> dynamicItemConfigList;

  public DynamicItemConfigBatch(DynamicItemConfigBatch dynamicItemConfigBatch) {
    dynamicItemConfigList = new ArrayList<>();
    dynamicItemConfigList.addAll(dynamicItemConfigBatch.getDynamicItemConfigList());
  }

  public static DynamicItemConfigBatch of(List<DynamicItemConfig> dynamicItemConfigList) {
    return new DynamicItemConfigBatch(dynamicItemConfigList);
  }
}

