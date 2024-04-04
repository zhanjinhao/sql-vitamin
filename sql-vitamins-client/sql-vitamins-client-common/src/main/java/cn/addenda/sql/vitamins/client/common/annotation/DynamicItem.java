package cn.addenda.sql.vitamins.client.common.annotation;

import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemOperation;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/9 20:25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DynamicItem {

  DynamicItemOperation dynamicItemOperation();

  String tableName();

  String itemName();

  String itemValue();

  Class<?> itemValueClass();

  InsertAddSelectItemMode insertAddSelectItemMode();

  boolean duplicateKeyUpdate();

  UpdateItemMode updateItemMode();

}
