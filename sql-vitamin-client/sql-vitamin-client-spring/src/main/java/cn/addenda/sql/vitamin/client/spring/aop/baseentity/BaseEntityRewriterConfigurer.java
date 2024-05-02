package cn.addenda.sql.vitamin.client.spring.aop.baseentity;

import cn.addenda.sql.vitamin.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityRewriter;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntitySource;
import cn.addenda.sql.vitamin.rewriter.baseentity.DruidBaseEntityRewriter;
import cn.addenda.sql.vitamin.rewriter.convertor.DataConvertorRegistry;
import lombok.Getter;

import java.util.List;

/**
 * @author addenda
 * @since 2023/6/11 20:59
 */
public class BaseEntityRewriterConfigurer implements NamedConfigurer {

  @Getter
  private final BaseEntityRewriter baseEntityRewriter;

  public BaseEntityRewriterConfigurer() {
    this.baseEntityRewriter = new DruidBaseEntityRewriter();
  }

  public BaseEntityRewriterConfigurer(
      List<String> included, List<String> excluded,
      BaseEntitySource baseEntitySource, DataConvertorRegistry dataConvertorRegistry) {
    this.baseEntityRewriter = new DruidBaseEntityRewriter(
        included, excluded, baseEntitySource, dataConvertorRegistry);
  }

  public BaseEntityRewriterConfigurer(BaseEntityRewriter baseEntityRewriter) {
    this.baseEntityRewriter = baseEntityRewriter;
  }

}
