package cn.addenda.sql.vitamin.client.spring.aop.dynamic.tablename;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
@Configuration
public class DynamicTableNameConfigConfiguration implements ImportAware {

  protected AnnotationAttributes annotationAttributes;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
        importMetadata.getAnnotationAttributes(EnableDynamicTableName.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
          EnableDynamicTableName.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public DynamicTableNameAdvisor dynamicTableNameAdvisor() {
    DynamicTableNameAdvisor dynamicTableNameAdvisor = new DynamicTableNameAdvisor();
    SpringDynamicTableNameInterceptor springDynamicTableNameInterceptor = new SpringDynamicTableNameInterceptor();
    dynamicTableNameAdvisor.setAdvice(springDynamicTableNameInterceptor);
    dynamicTableNameAdvisor.setOrder(annotationAttributes.<Integer>getNumber("order"));
    return dynamicTableNameAdvisor;
  }

}
