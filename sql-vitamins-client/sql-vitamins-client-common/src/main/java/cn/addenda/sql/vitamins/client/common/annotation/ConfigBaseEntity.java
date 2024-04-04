package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.client.common.constant.BoolConfig;
import cn.addenda.sql.vitamins.client.common.constant.InsertAddSelectItemModeConfig;
import cn.addenda.sql.vitamins.client.common.constant.UpdateItemModeConfig;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/8 18:41
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigBaseEntity {

  Propagation propagation() default Propagation.NEW;

  BoolConfig disable() default BoolConfig.DEFAULT;

  InsertAddSelectItemModeConfig insertAddSelectItemMode() default InsertAddSelectItemModeConfig.DEFAULT;

  BoolConfig duplicateKeyUpdate() default BoolConfig.DEFAULT;

  UpdateItemModeConfig updateItemMode() default UpdateItemModeConfig.DEFAULT;

  String masterView() default "";

}
