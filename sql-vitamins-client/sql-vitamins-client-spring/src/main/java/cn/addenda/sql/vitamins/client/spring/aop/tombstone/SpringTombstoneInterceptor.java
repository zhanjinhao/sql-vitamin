package cn.addenda.sql.vitamins.client.spring.aop.tombstone;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigTombstone;
import cn.addenda.sql.vitamins.client.common.config.TombstoneConfigUtils;
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

    try {
      ConfigTombstone configTombstone = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigTombstone.class);
      TombstoneConfigUtils.pushTombstone(configTombstone.propagation());
      TombstoneConfigUtils.configTombstone(configTombstone);
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      TombstoneContext.pop();
    }
  }

}
