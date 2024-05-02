package cn.addenda.sql.vitamin.rewriter.dynamic.item;

import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.Item;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;

/**
 * @author addenda
 * @since 2023/4/30 16:56
 */
public interface DynamicItemRewriter {

  String insertAddItem(String sql, String tableName, Item item, InsertAddSelectItemMode insertAddSelectItemMode,
                       boolean duplicateKeyUpdate, UpdateItemMode updateItemMode);

  String updateAddItem(String sql, String tableName, Item item, UpdateItemMode updateItemMode);

}
