package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicConditionOperation;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicSQLContext;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/9 20:16
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DynamicCondition {

  DynamicConditionOperation operation();

  String name() default DynamicSQLContext.ALL_TABLE;

  String condition();

}
