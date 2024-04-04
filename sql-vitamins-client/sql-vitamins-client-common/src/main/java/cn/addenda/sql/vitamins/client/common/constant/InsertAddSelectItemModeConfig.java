package cn.addenda.sql.vitamins.client.common.constant;

import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;

/**
 * @author addenda
 * @since 2024/3/27 22:19
 */
public enum InsertAddSelectItemModeConfig {

  VALUE,
  DB,
  DEFAULT;

  public static InsertAddSelectItemMode toInsertAddSelectItemMode(
      InsertAddSelectItemModeConfig insertAddSelectItemModeConfig) {
    if (insertAddSelectItemModeConfig == DEFAULT) {
      return null;
    } else if (insertAddSelectItemModeConfig == VALUE) {
      return InsertAddSelectItemMode.VALUE;
    } else if (insertAddSelectItemModeConfig == DB) {
      return InsertAddSelectItemMode.DB;
    }
    return null;
  }

}
