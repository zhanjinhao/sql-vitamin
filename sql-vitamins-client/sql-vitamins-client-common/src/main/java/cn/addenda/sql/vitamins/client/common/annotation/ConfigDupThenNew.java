package cn.addenda.sql.vitamins.client.common.annotation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/11 11:57
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigDupThenNew {

  boolean value() default true;

}
