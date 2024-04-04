package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.rewriter.lockingread.LockingReadContext;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/8 22:57
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigLockingRead {

  Propagation propagation() default Propagation.NEW;

  String value() default LockingReadContext.W_LOCK;

}
