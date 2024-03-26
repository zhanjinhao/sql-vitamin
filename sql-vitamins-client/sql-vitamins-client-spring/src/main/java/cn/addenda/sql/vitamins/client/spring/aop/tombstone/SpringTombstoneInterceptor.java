package cn.addenda.sql.vitamins.client.spring.aop.tombstone;

import cn.addenda.sql.vitamins.client.common.ConfigContextUtils;
import cn.addenda.sql.vitamins.client.common.annotation.ConfigJoinUseSubQuery;
import cn.addenda.sql.vitamins.client.common.annotation.DisableTombstone;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsSpringInterceptor;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneContext;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringTombstoneInterceptor extends AbstractSqlVitaminsSpringInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();
    Propagation propagation = extract(method, aClass);

    ConfigContextUtils.pushTombstone(propagation);
    try {
      ConfigContextUtils.configTombstone(propagation,
        SpringAnnotationUtils.extractAnnotation(method, aClass, DisableTombstone.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigJoinUseSubQuery.class));

      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      TombstoneContext.pop();
    }
  }

}
