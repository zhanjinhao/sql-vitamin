package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/8 22:11
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigUpdateItemMode {

  /**
   * update 语句或 insert 语句的on duplicate key update 部分更新策略<br/>
   * - ALL : 无论值是什么都更新。<br/>
   * - NOT_NULL ：仅非空时更新。<br/>
   * - NOT_EMPTY ：仅非空字符串时更新。
   */
  UpdateItemMode value() default UpdateItemMode.NOT_NULL;

}
