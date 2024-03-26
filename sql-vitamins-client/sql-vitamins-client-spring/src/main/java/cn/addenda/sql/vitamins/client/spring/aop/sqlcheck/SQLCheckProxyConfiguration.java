package cn.addenda.sql.vitamins.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsBeanPostProcessor;
import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SQLCheckException;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SQLCheckInterceptor;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SQLChecker;
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
public class SQLCheckProxyConfiguration implements ImportAware {

  protected AnnotationAttributes annotationAttributes;

  private Map<String, SQLCheckConfigurer> sqlCheckConfigurerMap;

  private int order;
  private boolean removeEnter;
  private SQLChecker sqlChecker;
  private boolean checkAllColumn;
  private boolean checkExactIdentifier;
  private boolean checkDmlCondition;

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.annotationAttributes = AnnotationAttributes.fromMap(
      importMetadata.getAnnotationAttributes(EnableSQLCheck.class.getName(), false));
    if (this.annotationAttributes == null) {
      throw new IllegalArgumentException(
        EnableSQLCheck.class.getName() + " is not present on importing class " + importMetadata.getClassName());
    }
  }

  @Bean
  public SQLCheckBeanPostProcessor sqlCheckBeanPostProcessor(BeanFactory beanFactory) {
    setSqlChecker(beanFactory);

    this.order = annotationAttributes.getNumber("order");
    this.removeEnter = annotationAttributes.getBoolean("removeEnter");
    this.checkAllColumn = annotationAttributes.getBoolean("checkAllColumn");
    this.checkExactIdentifier = annotationAttributes.getBoolean("checkExactIdentifier");
    this.checkDmlCondition = annotationAttributes.getBoolean("checkDmlCondition");
    return new SQLCheckBeanPostProcessor();
  }

  private void setSqlChecker(BeanFactory beanFactory) {
    String sqlCheckerName = annotationAttributes.getString("sqlChecker");
    SQLCheckConfigurer sqlCheckConfigurer;
    if (sqlCheckConfigurerMap != null &&
      (sqlCheckConfigurer = sqlCheckConfigurerMap.get(sqlCheckerName)) != null) {
      sqlChecker = sqlCheckConfigurer.getSqlChecker();
    } else {
      try {
        sqlChecker = beanFactory.getBean(sqlCheckerName, SQLChecker.class);
      } catch (Exception e) {
        String msg = String.format("无法获取配置的%s：[%s]", SQLChecker.class.getName(), sqlCheckerName);
        throw new SQLCheckException(msg, e);
      }
    }
    if (sqlChecker == null) {
      String msg = String.format("无法获取配置的%s：[%s]", SQLChecker.class.getName(), sqlCheckerName);
      throw new SQLCheckException(msg);
    }
  }

  @Autowired(required = false)
  void setConfigurers(List<SQLCheckConfigurer> configurers) {
    sqlCheckConfigurerMap = configurers.stream().collect(Collectors.toMap(NamedConfigurer::getName, a -> a));
  }

  public class SQLCheckBeanPostProcessor extends AbstractSqlVitaminsBeanPostProcessor<SQLCheckInterceptor> {

    @Override
    protected SQLCheckInterceptor getInterceptor() {
      return new SQLCheckInterceptor(removeEnter, sqlChecker, checkAllColumn,
        checkExactIdentifier, checkDmlCondition);
    }

    @Override
    public int getOrder() {
      return order;
    }
  }

}
