package cn.addenda.sql.vitamin.client.spring.aop.tombstone;

import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminSpringInterceptor;
import cn.addenda.sql.vitamin.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigTombstone;
import cn.addenda.sql.vitamin.client.common.config.TombstoneConfigUtils;
import cn.addenda.sql.vitamin.rewriter.tombstone.TombstoneContext;
import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringTombstoneInterceptor extends AbstractSqlVitaminSpringInterceptor {

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
