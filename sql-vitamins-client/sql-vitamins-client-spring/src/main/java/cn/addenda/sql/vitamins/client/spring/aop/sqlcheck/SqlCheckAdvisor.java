package cn.addenda.sql.vitamins.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigSqlCheck;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:52
 */
public class SqlCheckAdvisor extends AbstractBeanFactoryPointcutAdvisor {

  @Override
  public Pointcut getPointcut() {
    return new SqlCheckPointcut();
  }

  public static class SqlCheckPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      return SpringAnnotationUtils.annotationExists(method, targetClass, ConfigSqlCheck.class);
    }

  }

}
