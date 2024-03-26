package cn.addenda.sql.vitamins.client.spring.aop.dynamicsql;

import cn.addenda.sql.vitamins.client.common.ConfigContextUtils;
import cn.addenda.sql.vitamins.client.common.annotation.*;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsSpringInterceptor;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicSQLContext;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringDynamicSQLInterceptor extends AbstractSqlVitaminsSpringInterceptor {

  @Setter
  private DataConvertorRegistry dataConvertorRegistry;

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();
    Propagation propagation = extract(method, aClass);

    ConfigContextUtils.pushDynamicSQL(propagation);
    try {
      ConfigContextUtils.configDynamicSQL(propagation, dataConvertorRegistry,
        SpringAnnotationUtils.extractAnnotation(method, aClass, DynamicConditions.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigJoinUseSubQuery.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, DynamicItems.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigDupThenNew.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigDuplicateKeyUpdate.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigUpdateItemMode.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigInsertSelectAddItemMode.class));
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      DynamicSQLContext.pop();
    }
  }

}
