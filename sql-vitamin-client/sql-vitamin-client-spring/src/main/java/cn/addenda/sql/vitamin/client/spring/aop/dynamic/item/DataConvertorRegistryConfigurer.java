package cn.addenda.sql.vitamin.client.spring.aop.dynamic.item;

import cn.addenda.sql.vitamin.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamin.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.convertor.DefaultDataConvertorRegistry;
import lombok.Getter;

/**
 * @author addenda
 * @since 2022/11/30 19:21
 */
public class DataConvertorRegistryConfigurer implements NamedConfigurer {

  @Getter
  private final DataConvertorRegistry dataConvertorRegistry;

  public DataConvertorRegistryConfigurer() {
    this.dataConvertorRegistry = new DefaultDataConvertorRegistry();
  }

  public DataConvertorRegistryConfigurer(DataConvertorRegistry dataConvertorRegistry) {
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

}
