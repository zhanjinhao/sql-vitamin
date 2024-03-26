package cn.addenda.sql.vitamins.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamins.client.common.ConfigContextUtils;
import cn.addenda.sql.vitamins.client.common.annotation.UnCheckAllColumn;
import cn.addenda.sql.vitamins.client.common.annotation.UnCheckDmlCondition;
import cn.addenda.sql.vitamins.client.common.annotation.UnCheckExactIdentifier;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsSpringInterceptor;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SQLCheckContext;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringSQLCheckInterceptor extends AbstractSqlVitaminsSpringInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();
    Propagation propagation = extract(method, aClass);

    ConfigContextUtils.pushSQLCheck(propagation);
    try {
      ConfigContextUtils.configSQLCheck(propagation,
        SpringAnnotationUtils.extractAnnotation(method, aClass, UnCheckAllColumn.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, UnCheckExactIdentifier.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, UnCheckDmlCondition.class));

      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      SQLCheckContext.pop();
    }
  }

}
