package cn.addenda.sql.vitamin.client.spring.aop.dynamic.suffix;

import cn.addenda.sql.vitamin.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigDynamicSuffix;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:52
 */
public class DynamicSuffixAdvisor extends AbstractBeanFactoryPointcutAdvisor {

  @Override
  public Pointcut getPointcut() {
    return new DynamicSuffixPointcut();
  }

  public static class DynamicSuffixPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      return SpringAnnotationUtils.annotationExists(method, targetClass, ConfigDynamicSuffix.class);
    }

  }

}
