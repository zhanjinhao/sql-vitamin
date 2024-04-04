package cn.addenda.sql.vitamins.client.spring.aop.lockingread;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigLockingRead;
import cn.addenda.sql.vitamins.client.common.config.LockingReadConfigUtils;
import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsSpringInterceptor;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamins.rewriter.lockingread.LockingReadContext;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringLockingReadInterceptor extends AbstractSqlVitaminsSpringInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();

    try {
      ConfigLockingRead configLockingRead = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigLockingRead.class);
      LockingReadConfigUtils.pushLockingRead(configLockingRead.propagation());
      LockingReadConfigUtils.configLockingRead(configLockingRead);
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      LockingReadContext.pop();
    }
  }

}
