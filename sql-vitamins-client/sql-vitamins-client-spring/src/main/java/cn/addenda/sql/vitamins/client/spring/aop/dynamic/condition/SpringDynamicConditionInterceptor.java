package cn.addenda.sql.vitamins.client.spring.aop.dynamic.condition;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigDynamicCondition;
import cn.addenda.sql.vitamins.client.common.config.DynamicConditionConfigUtils;
import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsSpringInterceptor;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionContext;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringDynamicConditionInterceptor extends AbstractSqlVitaminsSpringInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();

    try {
      ConfigDynamicCondition configDynamicCondition = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigDynamicCondition.class);
      DynamicConditionConfigUtils.pushDynamicCondition(configDynamicCondition.propagation());
      DynamicConditionConfigUtils.configDynamicCondition(configDynamicCondition);
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      DynamicConditionContext.pop();
    }
  }

}
