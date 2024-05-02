package cn.addenda.sql.vitamin.client.spring.aop.dynamic.condition;

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
public class DynamicConditionConfigConfiguration implements ImportAware {

  protected AnnotationAttributes annotationAttributes;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
        importMetadata.getAnnotationAttributes(EnableDynamicCondition.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
          EnableDynamicCondition.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public DynamicConditionAdvisor dynamicConditionAdvisor() {
    DynamicConditionAdvisor argResLogAdvisor = new DynamicConditionAdvisor();
    SpringDynamicConditionInterceptor springDynamicConditionInterceptor = new SpringDynamicConditionInterceptor();
    argResLogAdvisor.setAdvice(springDynamicConditionInterceptor);
    argResLogAdvisor.setOrder(annotationAttributes.<Integer>getNumber("order"));
    return argResLogAdvisor;
  }

}
