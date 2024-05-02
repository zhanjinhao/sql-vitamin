package cn.addenda.sql.vitamin.client.spring.aop.baseentity;

import cn.addenda.sql.vitamin.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.SqlVitaminAopMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/11 21:16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({BaseEntitySelector.class})
public @interface EnableBaseEntity {

  int order() default Ordered.LOWEST_PRECEDENCE;

  SqlVitaminAopMode sqlVitaminAopMode() default SqlVitaminAopMode.PROXY_CONFIG;

  boolean removeEnter() default true;

  /**
   * 原始SQL -> BaseEntity SQL的重写器
   */
  String baseEntityRewriter() default NamedConfigurer.DEFAULT;

  boolean disable() default false;

  boolean selectDisable() default true;

  boolean compatibleMode() default false;

  boolean duplicateKeyUpdate() default false;

  InsertAddSelectItemMode insertAddSelectItemMode() default InsertAddSelectItemMode.VALUE;

  UpdateItemMode updateItemMode() default UpdateItemMode.NOT_NULL;

}
