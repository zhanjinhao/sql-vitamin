package cn.addenda.sql.vitamins.rewriter.tombstone;

import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.visitor.item.Item;

import java.util.List;

/**
 * @author addenda
 * @since 2023/5/20 10:37
 */
public abstract class AbstractTombstoneRewriter implements TombstoneRewriter {

  protected static final String TOMBSTONE_NAME = "if_del";
  protected static final Integer NON_TOMBSTONE_VALUE = 0;
  protected static final Integer TOMBSTONE_VALUE = 1;
  protected static final String NON_TOMBSTONE = TOMBSTONE_NAME + "=" + NON_TOMBSTONE_VALUE;
  protected static final String TOMBSTONE = TOMBSTONE_NAME + "=" + TOMBSTONE_VALUE;
  protected static final Item TOMBSTONE_ITEM = new Item(TOMBSTONE_NAME, NON_TOMBSTONE_VALUE);

  /**
   * 逻辑删除的表
   */
  protected final List<String> included;

  /**
   * 非逻辑删除的表
   */
  protected final List<String> notIncluded;

  protected final DataConvertorRegistry dataConvertorRegistry;

  protected AbstractTombstoneRewriter(List<String> included, List<String> notIncluded, DataConvertorRegistry dataConvertorRegistry) {
    this.included = included;
    this.notIncluded = notIncluded;
    this.dataConvertorRegistry = dataConvertorRegistry;
  }
}
