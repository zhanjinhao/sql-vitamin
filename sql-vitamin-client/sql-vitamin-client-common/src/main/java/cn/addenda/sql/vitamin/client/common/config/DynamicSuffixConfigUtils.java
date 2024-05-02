package cn.addenda.sql.vitamin.client.common.config;

import cn.addenda.sql.vitamin.client.common.constant.Propagation;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigDynamicSuffix;
import cn.addenda.sql.vitamin.rewriter.dynamic.suffix.DynamicSuffixConfig;
import cn.addenda.sql.vitamin.rewriter.dynamic.suffix.DynamicSuffixContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author addenda
 * @since 2023/6/11 16:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicSuffixConfigUtils {

  public static void configSuffixConfig(Propagation propagation, DynamicSuffixConfig dynamicSuffixConfig) {
    Propagation.assertNotNull(propagation);
    String suffix = dynamicSuffixConfig.getSuffix();
    if (suffix != null) {
      Propagation.configWithPropagation(propagation, suffix,
          DynamicSuffixContext::setSuffix, DynamicSuffixContext::getSuffix);
    }
  }

  public static void configSuffixConfig(ConfigDynamicSuffix configDynamicSuffix) {
    Propagation propagation = configDynamicSuffix.propagation();
    configSuffixConfig(propagation, DynamicSuffixConfig.of(configDynamicSuffix.value()));
  }

  public static void pushDynamicSuffix(Propagation propagation) {
    Propagation.assertNotNull(propagation);
    // NEW 压入新的
    if (propagation == Propagation.NEW || !DynamicSuffixContext.contextActive()) {
      DynamicSuffixContext.push(new DynamicSuffixConfig());
    }
    // MERGE_* 压入带参数的
    else {
      DynamicSuffixConfig dynamicSuffixConfig = DynamicSuffixContext.peek();
      DynamicSuffixContext.push(new DynamicSuffixConfig(dynamicSuffixConfig));
    }
  }

}
