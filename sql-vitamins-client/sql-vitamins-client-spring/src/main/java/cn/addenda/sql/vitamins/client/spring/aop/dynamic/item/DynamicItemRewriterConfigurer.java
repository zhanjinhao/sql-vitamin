package cn.addenda.sql.vitamins.client.spring.aop.dynamic.item;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DruidDynamicItemRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemRewriter;
import lombok.Getter;

import java.util.List;

/**
 * @author addenda
 * @since 2023/6/13 21:07
 */
public class DynamicItemRewriterConfigurer implements NamedConfigurer {

  @Getter
  private final DynamicItemRewriter dynamicItemRewriter;

  public DynamicItemRewriterConfigurer() {
    this.dynamicItemRewriter = new DruidDynamicItemRewriter(new DefaultDataConvertorRegistry());
  }

  public DynamicItemRewriterConfigurer(DataConvertorRegistry dataConvertorRegistry) {
    this.dynamicItemRewriter = new DruidDynamicItemRewriter(dataConvertorRegistry);
  }

  public DynamicItemRewriterConfigurer(DynamicItemRewriter dynamicItemRewriter) {
    this.dynamicItemRewriter = dynamicItemRewriter;
  }

  public DynamicItemRewriterConfigurer(List<String> notIncluded, DataConvertorRegistry dataConvertorRegistry) {
    this.dynamicItemRewriter = new DruidDynamicItemRewriter(notIncluded, dataConvertorRegistry);
  }

}
