package cn.addenda.sql.vitamins.client.spring.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2023/6/10 20:22
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringAnnotationUtils {

  public static <T extends Annotation> boolean annotationExists(Method method, Class<?> targetClass, Class<T> clazz) {
    Method actualMethod = AopUtils.getMostSpecificMethod(method, targetClass);
    T annotation = AnnotationUtils.findAnnotation(actualMethod, clazz);
    if (annotation == null) {
      annotation = AnnotationUtils.findAnnotation(targetClass, clazz);
    }

    return annotation != null;
  }

  public static <T extends Annotation> T extractAnnotation(Method method, Class<?> targetClass, Class<T> clazz) {
    Method actualMethod = AopUtils.getMostSpecificMethod(method, targetClass);
    T annotation = AnnotationUtils.findAnnotation(actualMethod, clazz);
    if (annotation == null) {
      annotation = AnnotationUtils.findAnnotation(targetClass, clazz);
    }

    return annotation;
  }

}
