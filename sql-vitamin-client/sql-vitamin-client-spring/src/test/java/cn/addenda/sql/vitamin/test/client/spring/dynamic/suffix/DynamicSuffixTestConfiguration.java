package cn.addenda.sql.vitamin.test.client.spring.dynamic.suffix;

import cn.addenda.sql.vitamin.client.spring.aop.baseentity.BaseEntityRewriterConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.baseentity.EnableBaseEntity;
import cn.addenda.sql.vitamin.client.spring.aop.dynamic.condition.DynamicConditionRewriterConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.dynamic.condition.EnableDynamicCondition;
import cn.addenda.sql.vitamin.client.spring.aop.dynamic.item.DataConvertorRegistryConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.dynamic.item.DynamicItemRewriterConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.dynamic.item.EnableDynamicItem;
import cn.addenda.sql.vitamin.client.spring.aop.dynamic.suffix.EnableDynamicSuffix;
import cn.addenda.sql.vitamin.client.spring.aop.tombstone.EnableTombstone;
import cn.addenda.sql.vitamin.client.spring.aop.tombstone.TombstoneRewriterConfigurer;
import cn.addenda.sql.vitamin.test.client.spring.DruidDataSourceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author addenda
 * @since 2023/7/24 9:49
 */
@Configuration
@EnableTombstone(compatibleMode = true)
@EnableBaseEntity(compatibleMode = true, selectDisable = false)
@EnableDynamicCondition
@EnableDynamicItem
@EnableDynamicSuffix
public class DynamicSuffixTestConfiguration {

  @Bean
  public DataSource dataSource() {
    return DruidDataSourceManager.getDataSource("vitamin_dynamicsuffix");
  }

  @Bean
  public TombstoneRewriterConfigurer tombstoneRewriterConfigurer() {
    return new TombstoneRewriterConfigurer();
  }

  @Bean
  public BaseEntityRewriterConfigurer baseEntityRewriterConfigurer() {
    return new BaseEntityRewriterConfigurer();
  }

  @Bean
  public DynamicConditionRewriterConfigurer dynamicConditionRewriterConfigurer() {
    return new DynamicConditionRewriterConfigurer();
  }

  @Bean
  public DynamicItemRewriterConfigurer dynamicItemRewriterConfigurer() {
    return new DynamicItemRewriterConfigurer();
  }

  @Bean
  public DataConvertorRegistryConfigurer dataConvertorRegistryConfigurer() {
    return new DataConvertorRegistryConfigurer();
  }

}
