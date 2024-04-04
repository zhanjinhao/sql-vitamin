package cn.addenda.sql.vitamins.client.spring.aop.dynamic.condition;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsBeanPostProcessor;
import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionSqlRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
@Slf4j
@Configuration
public class DynamicConditionProxyConfiguration implements ImportAware {

  private AnnotationAttributes annotationAttributes;

  Map<String, DynamicConditionRewriterConfigurer> dynamicConditionRewriterConfigurerMap;

  private int order;
  private boolean removeEnter;
  private DynamicConditionRewriter dynamicConditionRewriter;

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
  public DynamicConditionBeanPostProcessor dynamicSQLBeanPostProcessor(BeanFactory beanFactory) {
    setDynamicSQLRewriter(beanFactory);

    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    return new DynamicConditionBeanPostProcessor();
  }

  private void setDynamicSQLRewriter(BeanFactory beanFactory) {
    String dynamicConditionRewriterName = annotationAttributes.getString("dynamicConditionRewriter");
    DynamicConditionRewriterConfigurer dynamicConditionRewriterConfigurer;
    if (dynamicConditionRewriterConfigurerMap != null &&
        (dynamicConditionRewriterConfigurer = dynamicConditionRewriterConfigurerMap.get(dynamicConditionRewriterName)) != null) {
      dynamicConditionRewriter = dynamicConditionRewriterConfigurer.getDynamicConditionRewriter();
    } else {
      try {
        dynamicConditionRewriter = beanFactory.getBean(dynamicConditionRewriterName, DynamicConditionRewriter.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", DynamicConditionRewriter.class.getName(), dynamicConditionRewriterName);
        throw new DynamicSQLException(msg, e);
      }
    }
    if (dynamicConditionRewriter == null) {
      String msg = String.format("无法获取配置的%s：[%s]", DynamicConditionRewriter.class.getName(), dynamicConditionRewriterName);
      throw new DynamicSQLException(msg);
    }
  }

  @Autowired(required = false)
  public void setConfigurers(List<DynamicConditionRewriterConfigurer> configurers) {
    dynamicConditionRewriterConfigurerMap =
        configurers.stream().collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

  public class DynamicConditionBeanPostProcessor
      extends AbstractSqlVitaminsBeanPostProcessor<DynamicConditionSqlRewriter> {

    @Override
    protected DynamicConditionSqlRewriter getSqlRewriter() {
      return new DynamicConditionSqlRewriter(removeEnter, dynamicConditionRewriter);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }

}
