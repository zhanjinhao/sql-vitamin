package cn.addenda.sql.vitamins.client.spring.aop.tombstone;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsBeanPostProcessor;
import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneException;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneSqlInterceptor;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneRewriter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
@Configuration
public class TombstoneProxyConfiguration implements ImportAware {

  private AnnotationAttributes annotationAttributes;
  private Map<String, TombstoneRewriterConfigurer> tombstoneRewriterConfigurerMap;
  private int order;
  private boolean removeEnter;
  private TombstoneRewriter tombstoneRewriter;
  private boolean disable;
  private boolean joinUseSubQuery;
  private boolean includeDeleteTime;

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
  public TombstoneBeanPostProcessor tombstoneBeanPostProcessor(BeanFactory beanFactory) {
    setTombstoneSqlRewriter(beanFactory);

    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    this.disable = annotationAttributes.getBoolean("disable");
    this.joinUseSubQuery = annotationAttributes.getBoolean("joinUseSubQuery");
    this.includeDeleteTime = annotationAttributes.getBoolean("includeDeleteTime");
    return new TombstoneBeanPostProcessor();
  }

  private void setTombstoneSqlRewriter(BeanFactory beanFactory) {
    String tombstoneSqlRewriterName = annotationAttributes.getString("tombstoneSqlRewriter");
    TombstoneRewriterConfigurer tombstoneRewriterConfigurer;
    if (tombstoneRewriterConfigurerMap != null &&
        (tombstoneRewriterConfigurer = tombstoneRewriterConfigurerMap.get(tombstoneSqlRewriterName)) != null) {
      tombstoneRewriter = tombstoneRewriterConfigurer.getTombstoneRewriter();
    } else {
      try {
        tombstoneRewriter = beanFactory.getBean(tombstoneSqlRewriterName, TombstoneRewriter.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", TombstoneRewriter.class.getName(), tombstoneSqlRewriterName);
        throw new TombstoneException(msg, e);
      }
    }
    if (tombstoneRewriter == null) {
      String msg = String.format("无法获取配置的%s：[%s]", TombstoneRewriter.class.getName(), tombstoneSqlRewriterName);
      throw new TombstoneException(msg);
    }
  }

  @Autowired(required = false)
  public void setConfigurers(List<TombstoneRewriterConfigurer> configurers) {
    tombstoneRewriterConfigurerMap = configurers.stream()
        .collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

  public class TombstoneBeanPostProcessor
      extends AbstractSqlVitaminsBeanPostProcessor<TombstoneSqlInterceptor> {

    @Override
    protected TombstoneSqlInterceptor getSqlInterceptor() {
      return new TombstoneSqlInterceptor(removeEnter, tombstoneRewriter,
          disable, joinUseSubQuery, includeDeleteTime);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }

}
