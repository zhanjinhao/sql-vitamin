package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.client.common.constant.Propagation;

/**
 * @author addenda
 * @since 2023/6/10 20:36
 */
public @interface ConfigPropagation {

  Propagation value() default Propagation.NEW;

}
