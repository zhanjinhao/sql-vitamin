package cn.addenda.sql.vitamin.client.spring.aop;

import cn.addenda.sql.vitamin.rewriter.ChainedSqlInterceptor;
import cn.addenda.sql.vitamin.rewriter.SqlInterceptedDataSource;
import cn.addenda.sql.vitamin.rewriter.SqlInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import javax.sql.DataSource;

/**
 * @author addenda
 * @since 2023/6/13 21:17
 */
public abstract class AbstractSqlVitaminBeanPostProcessor<T extends SqlInterceptor> implements BeanPostProcessor, Ordered {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof DataSource) {
      if (bean instanceof SqlInterceptedDataSource) {
        SqlInterceptedDataSource sqlInterceptedDataSource = (SqlInterceptedDataSource) bean;
        ChainedSqlInterceptor chainedSqlInterceptor = (ChainedSqlInterceptor) sqlInterceptedDataSource.getSqlInterceptor();
        chainedSqlInterceptor.add(getSqlInterceptor());
      } else {
        ChainedSqlInterceptor chainedSqlInterceptor = new ChainedSqlInterceptor();
        chainedSqlInterceptor.add(getSqlInterceptor());
        bean = new SqlInterceptedDataSource((DataSource) bean, chainedSqlInterceptor);
      }
    }
    return bean;
  }

  protected abstract T getSqlInterceptor();

}
