package cn.addenda.sql.vitamins.client.spring.aop.dynamic.tablename;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.dynamic.tablename.DruidDynamicTableNameRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.tablename.DynamicTableNameRewriter;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/6/13 21:07
 */
public class DynamicTableNameRewriterConfigurer implements NamedConfigurer {

  @Getter
  private final DynamicTableNameRewriter dynamicTableNameRewriter;

  public DynamicTableNameRewriterConfigurer() {
    this.dynamicTableNameRewriter = new DruidDynamicTableNameRewriter();
  }

  public DynamicTableNameRewriterConfigurer(DynamicTableNameRewriter dynamicTableNameRewriter) {
    this.dynamicTableNameRewriter = dynamicTableNameRewriter;
  }

}
