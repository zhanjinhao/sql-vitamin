package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/8 22:11
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigInsertSelectAddItemMode {

  /**
   * insert select 语句的更新 <br/>
   * - VALUE : 从 <br/>
   * - DB ：select 语句的返回值增加字段，insert 取增加的字段。<br/>
   */
  InsertAddSelectItemMode value() default InsertAddSelectItemMode.VALUE;

}
