package cn.addenda.sql.vitamins.client.spring.aop.dynamic.item;

import cn.addenda.sql.vitamins.client.common.annotation.*;
import cn.addenda.sql.vitamins.client.common.config.DynamicItemConfigUtils;
import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsSpringInterceptor;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemContext;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class SpringDynamicItemInterceptor extends AbstractSqlVitaminsSpringInterceptor {

  @Setter
  private DataConvertorRegistry dataConvertorRegistry;

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Class<?> aClass = invocation.getThis().getClass();
    Method method = invocation.getMethod();

    try {
      ConfigDynamicItem configDynamicItem = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigDynamicItem.class);
      DynamicItemConfigUtils.pushDynamicItem(configDynamicItem.propagation());
      DynamicItemConfigUtils.configDynamicItem(configDynamicItem, dataConvertorRegistry);
      return invocation.proceed();
    } catch (Throwable throwable) {
      throw ExceptionUtil.unwrapThrowable(throwable);
    } finally {
      DynamicItemContext.pop();
    }
  }

}
