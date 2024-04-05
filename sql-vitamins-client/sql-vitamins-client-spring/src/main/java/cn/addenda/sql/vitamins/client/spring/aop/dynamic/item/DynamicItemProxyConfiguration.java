package cn.addenda.sql.vitamins.client.spring.aop.dynamic.item;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsBeanPostProcessor;
import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemSqlInterceptor;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemRewriter;
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
public class DynamicItemProxyConfiguration implements ImportAware {

  private AnnotationAttributes annotationAttributes;

  private Map<String, DynamicItemRewriterConfigurer> dynamicItemRewriterConfigurerMap;

  private int order;
  private boolean removeEnter;
  private InsertAddSelectItemMode insertAddSelectItemMode;
  private boolean duplicateKeyUpdate;
  private UpdateItemMode updateItemMode;
  private DynamicItemRewriter dynamicItemRewriter;

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
  public DynamicItemBeanPostProcessor dynamicItemBeanPostProcessor(BeanFactory beanFactory) {
    setDynamicItemRewriter(beanFactory);

    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    this.insertAddSelectItemMode = annotationAttributes.getEnum("insertAddSelectItemMode");
    this.duplicateKeyUpdate = annotationAttributes.getBoolean("duplicateKeyUpdate");
    this.updateItemMode = annotationAttributes.getEnum("updateItemMode");
    return new DynamicItemBeanPostProcessor();
  }

  private void setDynamicItemRewriter(BeanFactory beanFactory) {
    String dynamicItemRewriterName = annotationAttributes.getString("dynamicItemRewriter");
    DynamicItemRewriterConfigurer dynamicItemRewriterConfigurer;
    if (dynamicItemRewriterConfigurerMap != null &&
        (dynamicItemRewriterConfigurer = dynamicItemRewriterConfigurerMap.get(dynamicItemRewriterName)) != null) {
      dynamicItemRewriter = dynamicItemRewriterConfigurer.getDynamicItemRewriter();
    } else {
      try {
        dynamicItemRewriter = beanFactory.getBean(dynamicItemRewriterName, DynamicItemRewriter.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", DynamicItemRewriter.class.getName(), dynamicItemRewriterName);
        throw new DynamicSQLException(msg, e);
      }
    }
    if (dynamicItemRewriter == null) {
      String msg = String.format("无法获取配置的%s：[%s]", DynamicItemRewriter.class.getName(), dynamicItemRewriterName);
      throw new DynamicSQLException(msg);
    }
  }

  @Autowired(required = false)
  public void setConfigurers(List<DynamicItemRewriterConfigurer> configurers) {
    dynamicItemRewriterConfigurerMap =
        configurers.stream().collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

  public class DynamicItemBeanPostProcessor
      extends AbstractSqlVitaminsBeanPostProcessor<DynamicItemSqlInterceptor> {

    @Override
    protected DynamicItemSqlInterceptor getSqlInterceptor() {
      return new DynamicItemSqlInterceptor(removeEnter,
          insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode, dynamicItemRewriter);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }


}
