package cn.addenda.sql.vitamin.client.spring.aop.dynamic.tablename;

import cn.addenda.sql.vitamin.client.common.annotation.ConfigDynamicTableName;
import cn.addenda.sql.vitamin.client.common.config.DynamicTableNameConfigUtils;
import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminSpringInterceptor;
import cn.addenda.sql.vitamin.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamin.rewriter.dynamic.tablename.DynamicTableNameContext;
import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringDynamicTableNameInterceptor extends AbstractSqlVitaminSpringInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();

    try {
      ConfigDynamicTableName configDynamicTableName = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigDynamicTableName.class);
      DynamicTableNameConfigUtils.pushDynamicTableName(configDynamicTableName.propagation());
      DynamicTableNameConfigUtils.configDynamicTableName(configDynamicTableName);
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      DynamicTableNameContext.pop();
    }
  }

}
