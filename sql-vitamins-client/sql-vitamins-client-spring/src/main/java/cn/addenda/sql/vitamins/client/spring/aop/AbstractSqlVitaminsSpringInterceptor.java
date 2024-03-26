package cn.addenda.sql.vitamins.client.spring.aop;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigPropagation;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2023/6/11 16:26
 */
public abstract class AbstractSqlVitaminsSpringInterceptor implements MethodInterceptor {

  protected Propagation extract(Method method, Class<?> aClass) {
    ConfigPropagation configPropagation = SpringAnnotationUtils.extractAnnotation(method, aClass, ConfigPropagation.class);
    Propagation propagation = Propagation.NEW;
    if (configPropagation != null) {
      propagation = configPropagation.value();
    }

    return propagation;
  }

}
