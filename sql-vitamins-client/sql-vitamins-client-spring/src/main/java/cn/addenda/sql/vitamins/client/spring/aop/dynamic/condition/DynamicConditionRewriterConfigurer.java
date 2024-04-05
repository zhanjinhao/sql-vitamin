package cn.addenda.sql.vitamins.client.spring.aop.dynamic.condition;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DruidDynamicConditionRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionRewriter;
import lombok.Getter;

import java.util.List;

/**
 * @author addenda
 * @since 2023/6/13 21:07
 */
public class DynamicConditionRewriterConfigurer implements NamedConfigurer {

  @Getter
  private final DynamicConditionRewriter dynamicConditionRewriter;

  public DynamicConditionRewriterConfigurer() {
    this.dynamicConditionRewriter = new DruidDynamicConditionRewriter();
  }

  public DynamicConditionRewriterConfigurer(List<String> notIncluded) {
    this.dynamicConditionRewriter = new DruidDynamicConditionRewriter(notIncluded);
  }

  public DynamicConditionRewriterConfigurer(DynamicConditionRewriter dynamicConditionRewriter) {
    this.dynamicConditionRewriter = dynamicConditionRewriter;
  }

}
