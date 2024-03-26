package cn.addenda.sql.vitamins.client.spring.aop.dynamicsql;

import cn.addenda.sql.vitamins.client.common.annotation.*;
import cn.addenda.sql.vitamins.client.spring.util.SpringAnnotationUtils;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:52
 */
public class DynamicSQLAdvisor extends AbstractBeanFactoryPointcutAdvisor {

  @Override
  public Pointcut getPointcut() {
    return new DynamicSQLPointcut();
  }

  public static class DynamicSQLPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      return SpringAnnotationUtils.annotationExists(method, targetClass, ConfigPropagation.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, DynamicConditions.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, DynamicItems.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, ConfigJoinUseSubQuery.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, ConfigDupThenNew.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, ConfigDuplicateKeyUpdate.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, ConfigUpdateItemMode.class)
        || SpringAnnotationUtils.annotationExists(method, targetClass, ConfigInsertSelectAddItemMode.class);
    }

  }

}
