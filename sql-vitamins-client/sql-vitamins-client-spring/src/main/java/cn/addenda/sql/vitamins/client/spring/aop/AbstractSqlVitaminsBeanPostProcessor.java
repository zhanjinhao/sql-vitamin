package cn.addenda.sql.vitamins.client.spring.aop;

import cn.addenda.sql.vitamins.rewriter.ChainedSqlRewriter;
import cn.addenda.sql.vitamins.rewriter.SqlRewritableDataSource;
import cn.addenda.sql.vitamins.rewriter.SqlRewriter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import javax.sql.DataSource;

/**
 * @author addenda
 * @since 2023/6/13 21:17
 */
public abstract class AbstractSqlVitaminsBeanPostProcessor<T extends SqlRewriter> implements BeanPostProcessor, Ordered {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof DataSource) {
      if (bean instanceof SqlRewritableDataSource) {
        SqlRewritableDataSource sqlRewritableDataSource = (SqlRewritableDataSource) bean;
        ChainedSqlRewriter sqlRewriter = (ChainedSqlRewriter) sqlRewritableDataSource.getSqlRewriter();
        sqlRewriter.add(getSqlRewriter());
      } else {
        ChainedSqlRewriter chainedSQLRewriter = new ChainedSqlRewriter();
        chainedSQLRewriter.add(getSqlRewriter());
        bean = new SqlRewritableDataSource((DataSource) bean, chainedSQLRewriter);
      }
    }
    return bean;
  }

  protected abstract T getSqlRewriter();

}
