package cn.addenda.sql.vitamin.client.spring.aop.dynamic.tablename;

import cn.addenda.sql.vitamin.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminBeanPostProcessor;
import cn.addenda.sql.vitamin.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamin.rewriter.dynamic.tablename.DynamicTableNameRewriter;
import cn.addenda.sql.vitamin.rewriter.dynamic.tablename.DynamicTableNameSqlInterceptor;
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
public class DynamicTableNameProxyConfiguration implements ImportAware {

  private AnnotationAttributes annotationAttributes;

  private Map<String, DynamicTableNameRewriterConfigurer> dynamicTableNameRewriterConfigurerMap;

  private int order;
  private boolean removeEnter;
  private DynamicTableNameRewriter dynamicTableNameRewriter;

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
  public DynamicTableNameBeanPostProcessor dynamicTableNameBeanPostProcessor(BeanFactory beanFactory) {
    setDynamicTableNameRewriter(beanFactory);

    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    return new DynamicTableNameBeanPostProcessor();
  }

  private void setDynamicTableNameRewriter(BeanFactory beanFactory) {
    String dynamicTableNameRewriterName = annotationAttributes.getString("dynamicTableNameRewriter");
    DynamicTableNameRewriterConfigurer dynamicTableNameRewriterConfigurer;
    if (dynamicTableNameRewriterConfigurerMap != null &&
        (dynamicTableNameRewriterConfigurer = dynamicTableNameRewriterConfigurerMap.get(dynamicTableNameRewriterName)) != null) {
      dynamicTableNameRewriter = dynamicTableNameRewriterConfigurer.getDynamicTableNameRewriter();
    } else {
      try {
        dynamicTableNameRewriter = beanFactory.getBean(dynamicTableNameRewriterName, DynamicTableNameRewriter.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", DynamicTableNameRewriter.class.getName(), dynamicTableNameRewriterName);
        throw new DynamicSQLException(msg, e);
      }
    }
    if (dynamicTableNameRewriter == null) {
      String msg = String.format("无法获取配置的%s：[%s]", DynamicTableNameRewriter.class.getName(), dynamicTableNameRewriterName);
      throw new DynamicSQLException(msg);
    }
  }

  @Autowired(required = false)
  public void setConfigurers(List<DynamicTableNameRewriterConfigurer> configurers) {
    dynamicTableNameRewriterConfigurerMap =
        configurers.stream().collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

  public class DynamicTableNameBeanPostProcessor
      extends AbstractSqlVitaminBeanPostProcessor<DynamicTableNameSqlInterceptor> {

    @Override
    protected DynamicTableNameSqlInterceptor getSqlInterceptor() {
      return new DynamicTableNameSqlInterceptor(removeEnter, dynamicTableNameRewriter);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }

}
