package cn.addenda.sql.vitamin.client.spring.aop.baseentity;

import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminBeanPostProcessor;
import cn.addenda.sql.vitamin.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityException;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntitySqlInterceptor;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityRewriter;
import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
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
public class BaseEntityProxyConfiguration implements ImportAware {

  private AnnotationAttributes annotationAttributes;

  private Map<String, BaseEntityRewriterConfigurer> baseEntityRewriterConfigurerMap;

  private boolean removeEnter;
  private int order;
  private BaseEntityRewriter baseEntityRewriter;

  private boolean disable;
  private boolean selectDisable;
  private boolean compatibleMode;

  private boolean duplicateKeyUpdate;

  private InsertAddSelectItemMode insertAddSelectItemMode;

  private UpdateItemMode updateItemMode;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
        importMetadata.getAnnotationAttributes(EnableBaseEntity.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
          EnableBaseEntity.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  public BaseEntityBeanPostProcessor baseEntityBeanPostProcessor(BeanFactory beanFactory) {
    setBaseEntityRewriter(beanFactory);

    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    this.disable = annotationAttributes.getBoolean("disable");
    this.selectDisable = annotationAttributes.getBoolean("selectDisable");
    this.compatibleMode = annotationAttributes.getBoolean("compatibleMode");
    this.insertAddSelectItemMode = annotationAttributes.getEnum("insertAddSelectItemMode");
    this.duplicateKeyUpdate = annotationAttributes.getBoolean("duplicateKeyUpdate");
    this.updateItemMode = annotationAttributes.getEnum("updateItemMode");
    return new BaseEntityBeanPostProcessor();
  }

  private void setBaseEntityRewriter(BeanFactory beanFactory) {
    String baseEntityRewriterName = annotationAttributes.getString("baseEntityRewriter");
    BaseEntityRewriterConfigurer baseEntityRewriterConfigurer;
    if (baseEntityRewriterConfigurerMap != null &&
        (baseEntityRewriterConfigurer = baseEntityRewriterConfigurerMap.get(baseEntityRewriterName)) != null) {
      baseEntityRewriter = baseEntityRewriterConfigurer.getBaseEntityRewriter();
    } else {
      try {
        baseEntityRewriter = beanFactory.getBean(baseEntityRewriterName, BaseEntityRewriter.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", BaseEntityRewriter.class.getName(), baseEntityRewriterName);
        throw new BaseEntityException(msg, e);
      }
    }
    if (baseEntityRewriter == null) {
      String msg = String.format("无法获取配置的%s：[%s]", BaseEntityRewriter.class.getName(), baseEntityRewriterName);
      throw new BaseEntityException(msg);
    }
  }

  @Autowired(required = false)
  public void setBaseEntityRewriterConfigurers(List<BaseEntityRewriterConfigurer> configurers) {
    baseEntityRewriterConfigurerMap = configurers.stream()
        .collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

  public class BaseEntityBeanPostProcessor
      extends AbstractSqlVitaminBeanPostProcessor<BaseEntitySqlInterceptor> {

    @Override
    protected BaseEntitySqlInterceptor getSqlInterceptor() {
      return new BaseEntitySqlInterceptor(removeEnter, disable, selectDisable, compatibleMode,
          baseEntityRewriter, insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }

}
