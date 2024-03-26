package cn.addenda.sql.vitamins.client.common.annotation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/8 22:10
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigDuplicateKeyUpdate {

  /**
   * 是否生成 on duplicate key update 部分。<br/>
   * - true：生成 <br/>
   * - false：不生成
   */
  boolean value() default true;

}
