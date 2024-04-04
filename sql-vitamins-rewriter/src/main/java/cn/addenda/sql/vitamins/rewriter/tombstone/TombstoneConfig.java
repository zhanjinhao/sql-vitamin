package cn.addenda.sql.vitamins.rewriter.tombstone;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TombstoneConfig {

  private Boolean disable;

  private Boolean joinUseSubQuery;

  public TombstoneConfig(TombstoneConfig tombstoneConfig) {
    this.disable = tombstoneConfig.disable;
    this.joinUseSubQuery = tombstoneConfig.joinUseSubQuery;
  }

  public static TombstoneConfig of(Boolean disable, Boolean joinUseSubQuery) {
    return new TombstoneConfig(disable, joinUseSubQuery);
  }

}
