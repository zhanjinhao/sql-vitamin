package cn.addenda.sql.vitamin.rewriter.dynamic.tablename;

import lombok.*;

import java.util.List;

/**
 * @author addenda
 * @since 2024/3/31 16:30
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DynamicTableNameConfigBatch {

  List<DynamicTableNameConfig> dynamicTableNameConfigList;

  public DynamicTableNameConfigBatch(DynamicTableNameConfigBatch dynamicTableNameConfigBatch) {
    this.dynamicTableNameConfigList.addAll(dynamicTableNameConfigBatch.dynamicTableNameConfigList);
  }

  public static DynamicTableNameConfigBatch of(List<DynamicTableNameConfig> dynamicTableNameConfigList) {
    return new DynamicTableNameConfigBatch(dynamicTableNameConfigList);
  }

}
