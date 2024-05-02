package cn.addenda.sql.vitamin.client.common.annotation;

import cn.addenda.sql.vitamin.client.common.constant.BoolConfig;
import cn.addenda.sql.vitamin.client.common.constant.Propagation;

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
  BoolConfig compatibleMode() default BoolConfig.DEFAULT;

  BoolConfig joinUseSubQuery() default BoolConfig.DEFAULT;

  BoolConfig includeDeleteTime() default BoolConfig.DEFAULT;

}
