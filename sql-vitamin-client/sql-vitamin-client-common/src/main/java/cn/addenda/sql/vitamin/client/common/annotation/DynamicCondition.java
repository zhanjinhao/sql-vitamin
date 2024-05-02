package cn.addenda.sql.vitamin.client.common.annotation;

import cn.addenda.sql.vitamin.rewriter.dynamic.condition.DynamicConditionOperation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/9 20:16
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DynamicCondition {

  DynamicConditionOperation dynamicConditionOperation();

  String tableOrViewName();

  String condition();

  boolean joinUseSubQuery();

}
