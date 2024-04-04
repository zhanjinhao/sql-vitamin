package cn.addenda.sql.vitamins.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamins.client.spring.aop.SqlVitaminsAopMode;
import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
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
@Import(SqlCheckSelector.class)
public @interface EnableSqlCheck {

  int order() default Ordered.LOWEST_PRECEDENCE;

  SqlVitaminsAopMode sqlVitaminsAopMode() default SqlVitaminsAopMode.PROXY_CONFIG;

  boolean removeEnter() default true;

  String sqlChecker() default NamedConfigurer.DEFAULT;

  boolean disable() default false;

  boolean checkAllColumn() default true;

  boolean checkExactIdentifier() default true;

  boolean checkDmlCondition() default true;
}
