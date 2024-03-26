package cn.addenda.sql.vitamins.client.spring.aop.baseentity;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntityRewriter;
import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntitySource;
import cn.addenda.sql.vitamins.rewriter.baseentity.DefaultBaseEntitySource;
import cn.addenda.sql.vitamins.rewriter.baseentity.DruidBaseEntityRewriter;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/6/11 20:59
 */
public class BaseEntityRewriterConfigurer implements NamedConfigurer {

  @Getter
  private final BaseEntityRewriter baseEntityRewriter;

  public BaseEntityRewriterConfigurer() {
    this.baseEntityRewriter = new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
  }

  public BaseEntityRewriterConfigurer(BaseEntitySource baseEntitySource) {
    this.baseEntityRewriter = new DruidBaseEntityRewriter(null, null, baseEntitySource, new DefaultDataConvertorRegistry());
  }

}
