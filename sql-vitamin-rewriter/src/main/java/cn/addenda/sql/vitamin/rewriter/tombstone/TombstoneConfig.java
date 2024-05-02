package cn.addenda.sql.vitamin.rewriter.tombstone;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TombstoneConfig {

  private Boolean disable;
  private Boolean compatibleMode;

  private Boolean joinUseSubQuery;

  private Boolean includeDeleteTime;

  public TombstoneConfig(TombstoneConfig tombstoneConfig) {
    this.disable = tombstoneConfig.disable;
    this.compatibleMode = tombstoneConfig.compatibleMode;
    this.joinUseSubQuery = tombstoneConfig.joinUseSubQuery;
    this.includeDeleteTime = tombstoneConfig.includeDeleteTime;
  }

  public static TombstoneConfig of(Boolean disable, Boolean compatibleMode, Boolean joinUseSubQuery, Boolean includeDeleteTime) {
    return new TombstoneConfig(disable, compatibleMode, joinUseSubQuery, includeDeleteTime);
  }

}
