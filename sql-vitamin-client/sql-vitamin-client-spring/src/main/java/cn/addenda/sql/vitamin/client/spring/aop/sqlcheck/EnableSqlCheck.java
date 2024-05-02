package cn.addenda.sql.vitamin.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamin.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.SqlVitaminAopMode;
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

  SqlVitaminAopMode sqlVitaminAopMode() default SqlVitaminAopMode.PROXY_CONFIG;

  boolean removeEnter() default true;

  String sqlChecker() default NamedConfigurer.DEFAULT;

  boolean disable() default false;

  boolean checkAllColumn() default true;

  boolean checkExactIdentifier() default true;

  boolean checkDmlCondition() default true;
}
