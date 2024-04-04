package cn.addenda.sql.vitamins.client.spring.aop.dynamic.item;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
@Configuration
public class DynamicItemConfigConfiguration implements ImportAware {

  private AnnotationAttributes annotationAttributes;

  private Map<String, DataConvertorRegistryConfigurer> dataConvertorRegistryConfigurerMap;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
        importMetadata.getAnnotationAttributes(EnableDynamicItem.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
          EnableDynamicItem.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public DynamicItemAdvisor dynamicSQLAdvisor(BeanFactory beanFactory) {
    DynamicItemAdvisor argResLogAdvisor = new DynamicItemAdvisor();
    SpringDynamicItemInterceptor springDynamicItemInterceptor = new SpringDynamicItemInterceptor();
    DataConvertorRegistry dataConvertorRegistry = getDataConvertorRegistry(beanFactory);
    springDynamicItemInterceptor.setDataConvertorRegistry(dataConvertorRegistry);
    argResLogAdvisor.setAdvice(springDynamicItemInterceptor);
    argResLogAdvisor.setOrder(annotationAttributes.<Integer>getNumber("order"));
    return argResLogAdvisor;
  }

  private DataConvertorRegistry getDataConvertorRegistry(BeanFactory beanFactory) {
    String dataConvertorRegistryName = annotationAttributes.getString("dataConvertorRegistry");
    DataConvertorRegistry dataConvertorRegistry;
    DataConvertorRegistryConfigurer dataConvertorRegistryConfigurer;
    if (dataConvertorRegistryConfigurerMap != null &&
        (dataConvertorRegistryConfigurer = dataConvertorRegistryConfigurerMap.get(dataConvertorRegistryName)) != null) {
      dataConvertorRegistry = dataConvertorRegistryConfigurer.getDataConvertorRegistry();
    } else {
      try {
        dataConvertorRegistry = beanFactory.getBean(dataConvertorRegistryName, DataConvertorRegistry.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", DataConvertorRegistry.class.getName(), dataConvertorRegistryName);
        throw new DynamicSQLException(msg, e);
      }
    }
    if (dataConvertorRegistry == null) {
      String msg = String.format("无法获取配置的%s：[%s]", DataConvertorRegistry.class.getName(), dataConvertorRegistryName);
      throw new DynamicSQLException(msg);
    }
    return dataConvertorRegistry;
  }

  @Autowired(required = false)
  public void setConfigurers(List<DataConvertorRegistryConfigurer> configurers) {
    dataConvertorRegistryConfigurerMap =
        configurers.stream().collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

}
