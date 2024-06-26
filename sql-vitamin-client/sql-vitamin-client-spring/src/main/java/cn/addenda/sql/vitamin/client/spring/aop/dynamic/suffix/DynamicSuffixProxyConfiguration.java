package cn.addenda.sql.vitamin.client.spring.aop.dynamic.suffix;

import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminBeanPostProcessor;
import cn.addenda.sql.vitamin.rewriter.dynamic.suffix.DynamicSuffixSqlInterceptor;
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
public class DynamicSuffixProxyConfiguration implements ImportAware {

  protected AnnotationAttributes annotationAttributes;

  private int order;
  private boolean removeEnter;

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
  public DynamicSuffixPostProcessor dynamicSuffixPostProcessor() {
    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    return new DynamicSuffixPostProcessor();
  }

  public class DynamicSuffixPostProcessor
      extends AbstractSqlVitaminBeanPostProcessor<DynamicSuffixSqlInterceptor> {

    @Override
    protected DynamicSuffixSqlInterceptor getSqlInterceptor() {
      return new DynamicSuffixSqlInterceptor(removeEnter);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }

}
