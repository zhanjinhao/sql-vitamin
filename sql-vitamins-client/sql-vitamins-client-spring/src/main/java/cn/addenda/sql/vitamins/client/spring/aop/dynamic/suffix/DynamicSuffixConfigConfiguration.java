package cn.addenda.sql.vitamins.client.spring.aop.dynamic.suffix;

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
public class DynamicSuffixConfigConfiguration implements ImportAware {

  protected AnnotationAttributes annotationAttributes;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
      importMetadata.getAnnotationAttributes(EnableDynamicSuffix.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
        EnableDynamicSuffix.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public DynamicSuffixAdvisor dynamicSuffixAdvisor() {
    DynamicSuffixAdvisor dynamicSuffixAdvisor = new DynamicSuffixAdvisor();
    dynamicSuffixAdvisor.setAdvice(new SpringDynamicSuffixInterceptor());
    if (this.annotationAttributes != null) {
      dynamicSuffixAdvisor.setOrder(annotationAttributes.<Integer>getNumber("order"));
    }
    return dynamicSuffixAdvisor;
  }

}
