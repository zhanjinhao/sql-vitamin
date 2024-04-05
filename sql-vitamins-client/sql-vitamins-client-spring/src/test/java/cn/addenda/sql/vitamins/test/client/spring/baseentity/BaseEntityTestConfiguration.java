package cn.addenda.sql.vitamins.test.client.spring.baseentity;

import cn.addenda.sql.vitamins.client.spring.aop.baseentity.BaseEntityRewriterConfigurer;
import cn.addenda.sql.vitamins.client.spring.aop.baseentity.EnableBaseEntity;
import cn.addenda.sql.vitamins.test.client.spring.DruidDataSourceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author addenda
 * @since 2023/7/24 9:49
 */
@Configuration
@EnableBaseEntity
public class BaseEntityTestConfiguration {

  @Bean
  public DataSource dataSource() {
    return DruidDataSourceManager.getDataSource("vitamins_baseentity");
  }

  @Bean
  public BaseEntityRewriterConfigurer baseEntityRewriterConfigurer() {
    return new BaseEntityRewriterConfigurer();
  }

}
