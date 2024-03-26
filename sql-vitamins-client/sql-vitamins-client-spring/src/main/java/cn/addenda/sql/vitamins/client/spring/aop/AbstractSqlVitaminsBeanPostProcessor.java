package cn.addenda.sql.vitamins.client.spring.aop;

import cn.addenda.sql.vitamins.rewriter.InterceptedDataSource;
import cn.addenda.sql.vitamins.rewriter.Interceptor;
import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author addenda
 * @since 2023/6/13 21:17
 */
public abstract class AbstractSqlVitaminsBeanPostProcessor<T extends Interceptor> implements BeanPostProcessor, Ordered {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof DataSource) {
      if (bean instanceof InterceptedDataSource) {
        InterceptedDataSource interceptedDataSource = (InterceptedDataSource) bean;
        InterceptedDataSource.InnerDataSourceProxyImpl dataSourceProxy = interceptedDataSource.getDataSourceProxy();
        List<Interceptor> interceptorList = dataSourceProxy.getInterceptorList();
        interceptorList.add(getInterceptor());
        interceptedDataSource.setDataSourceProxy(new InterceptedDataSource.InnerDataSourceProxyImpl(interceptorList));
      } else {
        bean = new InterceptedDataSource((DataSource) bean, ArrayUtils.asArrayList(getInterceptor()));
      }
    }
    return bean;
  }

  protected abstract T getInterceptor();

}
