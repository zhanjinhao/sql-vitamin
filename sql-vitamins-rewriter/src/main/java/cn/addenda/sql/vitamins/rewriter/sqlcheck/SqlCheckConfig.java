package cn.addenda.sql.vitamins.rewriter.sqlcheck;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SqlCheckConfig {
  private Boolean checkAllColumn;
  private Boolean checkExactIdentifier;
  private Boolean checkDmlCondition;

  public SqlCheckConfig(SqlCheckConfig sqlCheckConfig) {
    this.checkAllColumn = sqlCheckConfig.checkAllColumn;
    this.checkExactIdentifier = sqlCheckConfig.checkExactIdentifier;
    this.checkDmlCondition = sqlCheckConfig.checkDmlCondition;
  }

  public static SqlCheckConfig of(
      Boolean checkAllColumn, Boolean checkExactIdentifier, Boolean checkDmlCondition) {
    return new SqlCheckConfig(checkAllColumn, checkExactIdentifier, checkDmlCondition);
  }
}
