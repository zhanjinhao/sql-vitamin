package cn.addenda.sql.vitamin.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminSpringInterceptor;
import cn.addenda.sql.vitamin.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigSqlCheck;
import cn.addenda.sql.vitamin.client.common.config.SqlCheckConfigUtils;
import cn.addenda.sql.vitamin.rewriter.sqlcheck.SqlCheckContext;
import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringSqlCheckInterceptor extends AbstractSqlVitaminSpringInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();
    try {
      ConfigSqlCheck configSqlCheck = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigSqlCheck.class);
      SqlCheckConfigUtils.pushSqlCheck(configSqlCheck.propagation());
      SqlCheckConfigUtils.configSqlCheck(configSqlCheck);
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      SqlCheckContext.pop();
    }
  }

}
