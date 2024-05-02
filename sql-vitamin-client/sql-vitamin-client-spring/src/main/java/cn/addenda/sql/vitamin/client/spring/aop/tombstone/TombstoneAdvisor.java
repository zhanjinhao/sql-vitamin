package cn.addenda.sql.vitamin.client.spring.aop.tombstone;

import cn.addenda.sql.vitamin.client.spring.util.SpringAnnotationUtils;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigTombstone;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:52
 */
public class TombstoneAdvisor extends AbstractBeanFactoryPointcutAdvisor {

  @Override
  public Pointcut getPointcut() {
    return new TombstonePointcut();
  }

  public static class TombstonePointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      return SpringAnnotationUtils.annotationExists(method, targetClass, ConfigTombstone.class);
    }

  }

}
