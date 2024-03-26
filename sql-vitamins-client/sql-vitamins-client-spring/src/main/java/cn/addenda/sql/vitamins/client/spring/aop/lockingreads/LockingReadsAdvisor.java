package cn.addenda.sql.vitamins.client.spring.aop.lockingreads;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigLock;
import cn.addenda.sql.vitamins.client.common.annotation.ConfigPropagation;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:52
 */
public class LockingReadsAdvisor extends AbstractBeanFactoryPointcutAdvisor {

  @Override
  public Pointcut getPointcut() {
    return new LockingReadsPointcut();
  }

  public static class LockingReadsPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      return SpringAnnotationUtils.annotationExists(method, targetClass, ConfigPropagation.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, ConfigLock.class);
    }

  }

}
