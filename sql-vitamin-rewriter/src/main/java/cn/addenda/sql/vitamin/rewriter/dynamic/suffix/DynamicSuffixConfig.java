package cn.addenda.sql.vitamin.rewriter.dynamic.suffix;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DynamicSuffixConfig {

  private String suffix;

  public DynamicSuffixConfig(DynamicSuffixConfig dynamicSuffixConfig) {
    this.suffix = dynamicSuffixConfig.suffix;
  }

  public static DynamicSuffixConfig of(String suffix) {
    return new DynamicSuffixConfig(suffix);
  }
}
