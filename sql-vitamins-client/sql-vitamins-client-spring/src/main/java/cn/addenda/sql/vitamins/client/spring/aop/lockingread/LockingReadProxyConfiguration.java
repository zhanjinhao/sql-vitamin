package cn.addenda.sql.vitamins.client.spring.aop.lockingread;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsBeanPostProcessor;
import cn.addenda.sql.vitamins.rewriter.lockingread.LockingReadSqlRewriter;
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
public class LockingReadProxyConfiguration implements ImportAware {

  protected AnnotationAttributes annotationAttributes;

  private int order;
  private boolean removeEnter;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
      importMetadata.getAnnotationAttributes(EnableLockingRead.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
        EnableLockingRead.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public LockingReadPostProcessor lockingReadsPostProcessor() {
    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    return new LockingReadPostProcessor();
  }

  public class LockingReadPostProcessor
      extends AbstractSqlVitaminsBeanPostProcessor<LockingReadSqlRewriter> {

    @Override
    protected LockingReadSqlRewriter getSqlRewriter() {
      return new LockingReadSqlRewriter(removeEnter);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }

}
