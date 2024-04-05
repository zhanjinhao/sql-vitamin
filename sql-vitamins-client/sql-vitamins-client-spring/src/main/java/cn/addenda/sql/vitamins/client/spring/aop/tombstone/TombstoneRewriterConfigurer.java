package cn.addenda.sql.vitamins.client.spring.aop.tombstone;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.tombstone.DruidTombstoneRewriter;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneRewriter;
import lombok.Getter;

import java.util.List;

/**
 * @author addenda
 * @since 2023/6/14 21:26
 */
public class TombstoneRewriterConfigurer implements NamedConfigurer {

  @Getter
  private final TombstoneRewriter tombstoneRewriter;

  public TombstoneRewriterConfigurer() {
    this.tombstoneRewriter = new DruidTombstoneRewriter();
  }

  public TombstoneRewriterConfigurer(List<String> included, List<String> notIncluded, DataConvertorRegistry dataConvertorRegistry) {
    this.tombstoneRewriter = new DruidTombstoneRewriter(included, notIncluded, dataConvertorRegistry);
  }

  public TombstoneRewriterConfigurer(TombstoneRewriter tombstoneRewriter) {
    this.tombstoneRewriter = tombstoneRewriter;
  }

}
