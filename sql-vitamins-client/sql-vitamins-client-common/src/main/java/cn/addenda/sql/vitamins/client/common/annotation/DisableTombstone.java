package cn.addenda.sql.vitamins.client.common.annotation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/10 14:16
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableTombstone {
  boolean value() default true;
}
