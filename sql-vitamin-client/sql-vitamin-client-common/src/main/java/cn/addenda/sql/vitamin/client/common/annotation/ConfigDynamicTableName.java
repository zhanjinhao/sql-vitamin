package cn.addenda.sql.vitamin.client.common.annotation;

import cn.addenda.sql.vitamin.client.common.constant.Propagation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2024/3/31 16:50
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigDynamicTableName {

  Propagation propagation() default Propagation.NEW;

  DynamicTableName[] dynamicTableNames();

}
