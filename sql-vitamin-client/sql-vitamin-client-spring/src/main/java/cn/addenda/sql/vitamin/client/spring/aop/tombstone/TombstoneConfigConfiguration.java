package cn.addenda.sql.vitamin.client.spring.aop.tombstone;

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
public class TombstoneConfigConfiguration implements ImportAware {

  protected AnnotationAttributes annotationAttributes;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
      importMetadata.getAnnotationAttributes(EnableTombstone.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
        EnableTombstone.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public TombstoneAdvisor tombstoneAdvisor() {
    TombstoneAdvisor tombstoneAdvisor = new TombstoneAdvisor();
    tombstoneAdvisor.setAdvice(new SpringTombstoneInterceptor());
    if (this.annotationAttributes != null) {
      tombstoneAdvisor.setOrder(annotationAttributes.<Integer>getNumber("order"));
    }
    return tombstoneAdvisor;
  }

}
