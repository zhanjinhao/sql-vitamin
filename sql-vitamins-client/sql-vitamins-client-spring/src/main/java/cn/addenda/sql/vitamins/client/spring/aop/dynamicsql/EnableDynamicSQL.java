package cn.addenda.sql.vitamins.client.spring.aop.dynamicsql;

import cn.addenda.sql.vitamins.client.spring.aop.SqlVitaminsAopMode;
import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
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
@Import(DynamicSQLSelector.class)
public @interface EnableDynamicSQL {

  int order() default Ordered.LOWEST_PRECEDENCE;

  SqlVitaminsAopMode sqlVitaminsAopMode() default SqlVitaminsAopMode.PROXY_CONFIG;

  boolean removeEnter() default true;

  String dataConvertorRegistry() default NamedConfigurer.DEFAULT;

  String dynamicSQLRewriter() default NamedConfigurer.DEFAULT;

  boolean joinUseSubQuery() default false;

  boolean duplicateKeyUpdate() default false;

  InsertAddSelectItemMode insertSelectAddItemMode() default InsertAddSelectItemMode.VALUE;

  UpdateItemMode updateItemMode() default UpdateItemMode.NOT_NULL;

}
