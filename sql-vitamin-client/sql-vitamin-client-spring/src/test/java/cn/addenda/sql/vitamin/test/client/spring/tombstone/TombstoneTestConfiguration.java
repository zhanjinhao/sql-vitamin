package cn.addenda.sql.vitamin.test.client.spring.tombstone;

import cn.addenda.sql.vitamin.client.spring.aop.baseentity.BaseEntityRewriterConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.baseentity.EnableBaseEntity;
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
public class TombstoneTestConfiguration {

  @Bean
  public DataSource dataSource() {
    return DruidDataSourceManager.getDataSource("vitamin_tombstone");
  }

  @Bean
  public TombstoneRewriterConfigurer tombstoneRewriterConfigurer() {
    return new TombstoneRewriterConfigurer();
  }

  @Bean
  public BaseEntityRewriterConfigurer baseEntityRewriterConfigurer() {
    return new BaseEntityRewriterConfigurer();
  }

}
