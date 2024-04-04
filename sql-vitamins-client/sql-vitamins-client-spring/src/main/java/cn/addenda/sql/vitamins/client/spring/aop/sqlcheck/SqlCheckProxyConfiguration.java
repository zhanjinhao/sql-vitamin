package cn.addenda.sql.vitamins.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsBeanPostProcessor;
import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SqlCheckException;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SqlCheckSqlRewriter;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SqlChecker;
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
@Configuration
public class SqlCheckProxyConfiguration implements ImportAware {

  private AnnotationAttributes annotationAttributes;

  private Map<String, SqlCheckConfigurer> sqlCheckConfigurerMap;

  private int order;
  private boolean removeEnter;
  private SqlChecker sqlChecker;
  private boolean checkAllColumn;
  private boolean checkExactIdentifier;
  private boolean checkDmlCondition;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
        importMetadata.getAnnotationAttributes(EnableSqlCheck.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
          EnableSqlCheck.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  public SqlCheckBeanPostProcessor sqlCheckBeanPostProcessor(BeanFactory beanFactory) {
    setSqlChecker(beanFactory);

    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    this.checkAllColumn = annotationAttributes.getBoolean("checkAllColumn");
    this.checkExactIdentifier = annotationAttributes.getBoolean("checkExactIdentifier");
    this.checkDmlCondition = annotationAttributes.getBoolean("checkDmlCondition");
    return new SqlCheckBeanPostProcessor();
  }

  private void setSqlChecker(BeanFactory beanFactory) {
    String sqlCheckerName = annotationAttributes.getString("sqlChecker");
    SqlCheckConfigurer sqlCheckConfigurer;
    if (sqlCheckConfigurerMap != null &&
        (sqlCheckConfigurer = sqlCheckConfigurerMap.get(sqlCheckerName)) != null) {
      sqlChecker = sqlCheckConfigurer.getSqlChecker();
    } else {
      try {
        sqlChecker = beanFactory.getBean(sqlCheckerName, SqlChecker.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", SqlChecker.class.getName(), sqlCheckerName);
        throw new SqlCheckException(msg, e);
      }
    }
    if (sqlChecker == null) {
      String msg = String.format("无法获取配置的%s：[%s]", SqlChecker.class.getName(), sqlCheckerName);
      throw new SqlCheckException(msg);
    }
  }

  @Autowired(required = false)
  public void setConfigurers(List<SqlCheckConfigurer> configurers) {
    sqlCheckConfigurerMap = configurers.stream()
        .collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

  public class SqlCheckBeanPostProcessor
      extends AbstractSqlVitaminsBeanPostProcessor<SqlCheckSqlRewriter> {

    @Override
    protected SqlCheckSqlRewriter getSqlRewriter() {
      return new SqlCheckSqlRewriter(
          removeEnter, sqlChecker, checkAllColumn, checkExactIdentifier, checkDmlCondition);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }

}
