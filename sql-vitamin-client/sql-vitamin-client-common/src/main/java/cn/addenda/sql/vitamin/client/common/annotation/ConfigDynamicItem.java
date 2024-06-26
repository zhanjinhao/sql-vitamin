package cn.addenda.sql.vitamin.client.common.annotation;

import cn.addenda.sql.vitamin.client.common.constant.Propagation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2024/3/30 10:13
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigDynamicItem {

  Propagation propagation() default Propagation.NEW;

  DynamicItem[] dynamicItems();

}
