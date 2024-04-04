package cn.addenda.sql.vitamins.client.spring.aop.baseentity;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigBaseEntity;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:52
 */
public class BaseEntityAdvisor extends AbstractBeanFactoryPointcutAdvisor {

  @Override
  public Pointcut getPointcut() {
    return new BaseEntityPointcut();
  }

  public static class BaseEntityPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      return SpringAnnotationUtils.annotationExists(method, targetClass, ConfigBaseEntity.class);
    }

  }

}
