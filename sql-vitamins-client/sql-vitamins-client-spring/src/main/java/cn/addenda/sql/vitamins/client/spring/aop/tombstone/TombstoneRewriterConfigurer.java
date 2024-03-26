package cn.addenda.sql.vitamins.client.spring.aop.tombstone;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.tombstone.DruidTombstoneRewriter;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneRewriter;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/6/14 21:26
 */
public class TombstoneRewriterConfigurer implements NamedConfigurer {

  @Getter
  private final TombstoneRewriter tombstoneRewriter;

  public TombstoneRewriterConfigurer() {
    this.tombstoneRewriter = new DruidTombstoneRewriter(null, null, new DefaultDataConvertorRegistry());
  }

}
