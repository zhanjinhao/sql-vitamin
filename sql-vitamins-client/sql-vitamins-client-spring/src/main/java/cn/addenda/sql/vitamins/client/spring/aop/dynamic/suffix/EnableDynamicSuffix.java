package cn.addenda.sql.vitamins.client.spring.aop.dynamic.suffix;

import cn.addenda.sql.vitamins.client.spring.aop.SqlVitaminsAopMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/10 18:02
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicSuffixSelector.class)
public @interface EnableDynamicSuffix {

  SqlVitaminsAopMode sqlVitaminsAopMode() default SqlVitaminsAopMode.PROXY_CONFIG;

  boolean removeEnter() default true;

  int order() default Ordered.LOWEST_PRECEDENCE;

}
