package cn.addenda.sql.vitamin.client.common.annotation;

import cn.addenda.sql.vitamin.client.common.constant.BoolConfig;
import cn.addenda.sql.vitamin.client.common.constant.InsertAddSelectItemModeConfig;
import cn.addenda.sql.vitamin.client.common.constant.Propagation;
import cn.addenda.sql.vitamin.client.common.constant.UpdateItemModeConfig;

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
  BoolConfig compatibleMode() default BoolConfig.DEFAULT;

  InsertAddSelectItemModeConfig insertAddSelectItemMode() default InsertAddSelectItemModeConfig.DEFAULT;

  BoolConfig duplicateKeyUpdate() default BoolConfig.DEFAULT;

  UpdateItemModeConfig updateItemMode() default UpdateItemModeConfig.DEFAULT;

  String masterView() default "";

}
