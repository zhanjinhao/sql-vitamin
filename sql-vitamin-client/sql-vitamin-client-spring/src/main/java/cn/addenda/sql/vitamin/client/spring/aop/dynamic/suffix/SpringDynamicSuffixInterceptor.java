package cn.addenda.sql.vitamin.client.spring.aop.dynamic.suffix;

import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminSpringInterceptor;
import cn.addenda.sql.vitamin.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigDynamicSuffix;
import cn.addenda.sql.vitamin.client.common.config.DynamicSuffixConfigUtils;
import cn.addenda.sql.vitamin.rewriter.dynamic.suffix.DynamicSuffixContext;
import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringDynamicSuffixInterceptor extends AbstractSqlVitaminSpringInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();

    try {
      ConfigDynamicSuffix configDynamicSuffix = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigDynamicSuffix.class);
      DynamicSuffixConfigUtils.pushDynamicSuffix(configDynamicSuffix.propagation());
      DynamicSuffixConfigUtils.configSuffixConfig(configDynamicSuffix);
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      DynamicSuffixContext.pop();
    }
  }

}
