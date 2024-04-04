package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.client.common.constant.BoolConfig;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/10 14:16
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigTombstone {

  Propagation propagation() default Propagation.NEW;

  BoolConfig disable() default BoolConfig.DEFAULT;

  BoolConfig joinUseSubQuery() default BoolConfig.DEFAULT;

}
