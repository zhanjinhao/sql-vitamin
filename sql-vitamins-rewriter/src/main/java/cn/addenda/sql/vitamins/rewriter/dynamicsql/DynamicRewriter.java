package cn.addenda.sql.vitamins.rewriter.dynamicsql;

import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.Item;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;

/**
 * @author addenda
 * @since 2023/4/30 16:56
 */
public interface DynamicRewriter {

  String tableAddJoinCondition(String sql, String tableName, String condition, boolean useSubQuery);

  String viewAddJoinCondition(String sql, String tableName, String condition, boolean useSubQuery);

  String tableAddWhereCondition(String sql, String tableName, String condition);

  String viewAddWhereCondition(String sql, String tableName, String condition);

  String insertAddItem(String sql, String tableName, Item item, InsertAddSelectItemMode insertAddSelectItemMode,
                       boolean duplicateKeyUpdate, UpdateItemMode updateItemMode);

  String updateAddItem(String sql, String tableName, Item item, UpdateItemMode updateItemMode);

}
