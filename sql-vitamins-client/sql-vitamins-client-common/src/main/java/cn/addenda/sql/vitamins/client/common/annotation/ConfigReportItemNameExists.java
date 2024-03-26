package cn.addenda.sql.vitamins.client.common.annotation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/8 22:10
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigReportItemNameExists {

  /**
   * item存在时是否报告异常。<br/>
   * - true: 报告异常 <br/>
   * - false: 跳过当前item
   */
  boolean value() default true;

}
