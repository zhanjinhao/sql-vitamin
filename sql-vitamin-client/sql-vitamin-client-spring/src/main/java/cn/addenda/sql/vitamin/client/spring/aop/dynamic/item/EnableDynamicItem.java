package cn.addenda.sql.vitamin.client.spring.aop.dynamic.item;

import cn.addenda.sql.vitamin.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.SqlVitaminAopMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/10 18:02
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicItemSelector.class)
public @interface EnableDynamicItem {

  int order() default Ordered.LOWEST_PRECEDENCE;

  SqlVitaminAopMode sqlVitaminAopMode() default SqlVitaminAopMode.PROXY_CONFIG;

  boolean removeEnter() default true;

  InsertAddSelectItemMode insertAddSelectItemMode() default InsertAddSelectItemMode.VALUE;

  boolean duplicateKeyUpdate() default false;

  UpdateItemMode updateItemMode() default UpdateItemMode.NOT_NULL;

  String dataConvertorRegistry() default NamedConfigurer.DEFAULT;

  String dynamicItemRewriter() default NamedConfigurer.DEFAULT;

}
