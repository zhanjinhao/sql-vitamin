package cn.addenda.sql.vitamin.rewriter.dynamic.tablename;

import lombok.*;

/**
 * @author addenda
 * @since 2024/3/31 16:28
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DynamicTableNameConfig {

  private String originalTableName;
  private String targetTableName;

  public DynamicTableNameConfig(DynamicTableNameConfig dynamicTableNameConfig) {
    this.originalTableName = dynamicTableNameConfig.originalTableName;
    this.targetTableName = dynamicTableNameConfig.targetTableName;
  }

  public static DynamicTableNameConfig of(String originalTableName, String targetTableName) {
    return new DynamicTableNameConfig(originalTableName, targetTableName);
  }

}
