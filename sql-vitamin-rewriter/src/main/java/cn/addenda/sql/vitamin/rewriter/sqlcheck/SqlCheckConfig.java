package cn.addenda.sql.vitamin.rewriter.sqlcheck;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SqlCheckConfig {
  private Boolean disable;
  private Boolean checkAllColumn;
  private Boolean checkExactIdentifier;
  private Boolean checkDmlCondition;

  public SqlCheckConfig(SqlCheckConfig sqlCheckConfig) {
    this.disable = sqlCheckConfig.disable;
    this.checkAllColumn = sqlCheckConfig.checkAllColumn;
    this.checkExactIdentifier = sqlCheckConfig.checkExactIdentifier;
    this.checkDmlCondition = sqlCheckConfig.checkDmlCondition;
  }

  public static SqlCheckConfig of(
      Boolean disable, Boolean checkAllColumn, Boolean checkExactIdentifier, Boolean checkDmlCondition) {
    return new SqlCheckConfig(disable, checkAllColumn, checkExactIdentifier, checkDmlCondition);
  }
}
