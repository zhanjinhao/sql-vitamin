package cn.addenda.sql.vitamin.client.common.annotation;

import cn.addenda.sql.vitamin.client.common.constant.Propagation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/8 22:57
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigDynamicSuffix {

  Propagation propagation() default Propagation.NEW;

  String value();

}
