package cn.addenda.sql.vitamins.client.common.constant;

import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;

/**
 * @author addenda
 * @since 2024/3/27 22:17
 */
public enum UpdateItemModeConfig {

  ALL,
  NOT_NULL,
  /**
   * EMPTY的定义：""
   */
  NOT_EMPTY,
  DEFAULT;

  public static UpdateItemMode toUpdateItemMode(UpdateItemModeConfig updateItemModeConfig) {
    if (updateItemModeConfig == DEFAULT) {
      return null;
    } else if (updateItemModeConfig == ALL) {
      return UpdateItemMode.ALL;
    } else if (updateItemModeConfig == NOT_NULL) {
      return UpdateItemMode.NOT_NULL;
    } else if (updateItemModeConfig == NOT_EMPTY) {
      return UpdateItemMode.NOT_EMPTY;
    }
    return null;
  }
}
