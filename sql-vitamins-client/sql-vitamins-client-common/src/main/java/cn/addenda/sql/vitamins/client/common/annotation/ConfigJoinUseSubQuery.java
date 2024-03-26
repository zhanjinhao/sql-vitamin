package cn.addenda.sql.vitamins.client.common.annotation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/10 13:40
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigJoinUseSubQuery {

  boolean value() default true;

}
