package cn.addenda.sql.vitamins.client.spring.aop.dynamicsql;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DruidDynamicRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicRewriter;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/6/13 21:07
 */
public class DynamicSQLRewriterConfigurer implements NamedConfigurer {

  @Getter
  private final DynamicRewriter dynamicRewriter;

  public DynamicSQLRewriterConfigurer() {
    this.dynamicRewriter = new DruidDynamicRewriter(new DefaultDataConvertorRegistry());
  }

}
