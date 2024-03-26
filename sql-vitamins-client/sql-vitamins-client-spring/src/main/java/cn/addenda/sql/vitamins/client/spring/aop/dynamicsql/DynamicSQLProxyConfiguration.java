package cn.addenda.sql.vitamins.client.spring.aop.dynamicsql;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsBeanPostProcessor;
import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicSQLInterceptor;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicRewriter;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
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
public class DynamicSQLProxyConfiguration implements ImportAware {

  private AnnotationAttributes annotationAttributes;

  Map<String, DynamicSQLRewriterConfigurer> dynamicSQLRewriterConfigurerMap;

  private int order;
  private boolean removeEnter;
  private DynamicRewriter dynamicRewriter;
  private InsertAddSelectItemMode insertAddSelectItemMode;
  private boolean duplicateKeyUpdate;
  private UpdateItemMode updateItemMode;
  private boolean joinUseSubQuery;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
      importMetadata.getAnnotationAttributes(EnableDynamicSQL.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
        EnableDynamicSQL.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  public DynamicSQLBeanPostProcessor dynamicSQLBeanPostProcessor(BeanFactory beanFactory) {
    setDynamicSQLRewriter(beanFactory);

    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    this.insertAddSelectItemMode = annotationAttributes.getEnum("insertSelectAddItemMode");
    this.duplicateKeyUpdate = annotationAttributes.getBoolean("duplicateKeyUpdate");
    this.updateItemMode = annotationAttributes.getEnum("updateItemMode");
    this.joinUseSubQuery = annotationAttributes.getBoolean("joinUseSubQuery");
    return new DynamicSQLBeanPostProcessor();
  }

  private void setDynamicSQLRewriter(BeanFactory beanFactory) {
    String dynamicSQLRewriterName = annotationAttributes.getString("dynamicSQLRewriter");
    DynamicSQLRewriterConfigurer dynamicSQLRewriterConfigurer;
    if (dynamicSQLRewriterConfigurerMap != null &&
      (dynamicSQLRewriterConfigurer = dynamicSQLRewriterConfigurerMap.get(dynamicSQLRewriterName)) != null) {
      dynamicRewriter = dynamicSQLRewriterConfigurer.getDynamicRewriter();
    } else {
      try {
        dynamicRewriter = beanFactory.getBean(dynamicSQLRewriterName, DynamicRewriter.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", DynamicRewriter.class.getName(), dynamicSQLRewriterName);
        throw new DynamicSQLException(msg, e);
      }
    }
    if (dynamicRewriter == null) {
      String msg = String.format("无法获取配置的%s：[%s]", DynamicRewriter.class.getName(), dynamicSQLRewriterName);
      throw new DynamicSQLException(msg);
    }
  }

  @Autowired(required = false)
  void setConfigurers(List<DynamicSQLRewriterConfigurer> configurers) {
    dynamicSQLRewriterConfigurerMap = configurers.stream().collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

  public class DynamicSQLBeanPostProcessor extends AbstractSqlVitaminsBeanPostProcessor<DynamicSQLInterceptor> {

    @Override
    protected DynamicSQLInterceptor getInterceptor() {
      return new DynamicSQLInterceptor(removeEnter, dynamicRewriter, insertAddSelectItemMode,
        duplicateKeyUpdate, updateItemMode, joinUseSubQuery);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }


}
