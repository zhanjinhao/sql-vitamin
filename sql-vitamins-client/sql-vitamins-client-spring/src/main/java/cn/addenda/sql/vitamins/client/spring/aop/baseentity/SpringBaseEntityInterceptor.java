package cn.addenda.sql.vitamins.client.spring.aop.baseentity;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigBaseEntity;
import cn.addenda.sql.vitamins.client.common.config.BaseEntityConfigUtils;
import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsSpringInterceptor;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntityContext;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringBaseEntityInterceptor extends AbstractSqlVitaminsSpringInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();

    try {
      ConfigBaseEntity configBaseEntity = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigBaseEntity.class);
      BaseEntityConfigUtils.pushBaseEntity(configBaseEntity.propagation());
      BaseEntityConfigUtils.configBaseEntity(configBaseEntity);
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      BaseEntityContext.pop();
    }
  }

}
