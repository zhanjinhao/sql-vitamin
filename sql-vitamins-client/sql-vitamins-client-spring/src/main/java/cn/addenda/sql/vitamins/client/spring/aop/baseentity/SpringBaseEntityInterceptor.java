package cn.addenda.sql.vitamins.client.spring.aop.baseentity;

import cn.addenda.sql.vitamins.client.common.ConfigContextUtils;
import cn.addenda.sql.vitamins.client.common.annotation.*;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
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
    Propagation propagation = extract(method, aClass);

    ConfigContextUtils.pushBaseEntity(propagation);
    try {
      ConfigContextUtils.configBaseEntity(propagation,
        SpringAnnotationUtils.extractAnnotation(method, aClass, DisableBaseEntity.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigMasterView.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigDuplicateKeyUpdate.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigUpdateItemMode.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigReportItemNameExists.class),
        SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigInsertSelectAddItemMode.class));

      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      BaseEntityContext.pop();
    }
  }

}
