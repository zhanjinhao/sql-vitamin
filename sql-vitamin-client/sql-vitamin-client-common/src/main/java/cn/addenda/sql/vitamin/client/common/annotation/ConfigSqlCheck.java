package cn.addenda.sql.vitamin.client.common.annotation;

import cn.addenda.sql.vitamin.client.common.constant.BoolConfig;
import cn.addenda.sql.vitamin.client.common.constant.Propagation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2024/3/27 21:44
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigSqlCheck {

  Propagation propagation() default Propagation.NEW;

  BoolConfig disable() default BoolConfig.DEFAULT;

  BoolConfig checkAllColumn() default BoolConfig.DEFAULT;

  BoolConfig checkExactIdentifier() default BoolConfig.DEFAULT;

  BoolConfig checkDmlCondition() default BoolConfig.DEFAULT;

}
