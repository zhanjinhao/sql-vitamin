package cn.addenda.sql.vitamin.client.spring.aop.tombstone;

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
@Import(TombstoneSelector.class)
public @interface EnableTombstone {

  int order() default Ordered.LOWEST_PRECEDENCE;

  SqlVitaminAopMode sqlVitaminAopMode() default SqlVitaminAopMode.PROXY_CONFIG;

  boolean removeEnter() default true;

  boolean disable() default false;

  boolean compatibleMode() default false;

  String tombstoneSqlRewriter() default NamedConfigurer.DEFAULT;

  boolean joinUseSubQuery() default false;

  boolean includeDeleteTime() default false;

}
