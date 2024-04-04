package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.client.common.constant.Propagation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2024/3/30 10:13
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigDynamicCondition {

  Propagation propagation() default Propagation.NEW;

  DynamicCondition[] dynamicConditions();

}
