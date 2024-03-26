package cn.addenda.sql.vitamins.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigPropagation;
import cn.addenda.sql.vitamins.client.common.annotation.UnCheckAllColumn;
import cn.addenda.sql.vitamins.client.common.annotation.UnCheckDmlCondition;
import cn.addenda.sql.vitamins.client.common.annotation.UnCheckExactIdentifier;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:52
 */
public class SQLCheckAdvisor extends AbstractBeanFactoryPointcutAdvisor {

  @Override
  public Pointcut getPointcut() {
    return new SQLCheckPointcut();
  }

  public static class SQLCheckPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      return SpringAnnotationUtils.annotationExists(method, targetClass, ConfigPropagation.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, UnCheckAllColumn.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, UnCheckExactIdentifier.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, UnCheckDmlCondition.class);
    }

  }

}
