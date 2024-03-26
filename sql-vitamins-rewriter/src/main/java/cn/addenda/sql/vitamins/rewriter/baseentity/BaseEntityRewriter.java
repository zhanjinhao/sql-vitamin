package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;

/**
 * @author addenda
 * @since 2023/5/2 19:35
 */
public interface BaseEntityRewriter {

  String rewriteInsertSql(String sql, InsertAddSelectItemMode insertAddSelectItemMode,
                          boolean duplicateKeyUpdate, UpdateItemMode updateItemMode, boolean reportItemNameExists);

  String rewriteSelectSql(String sql, String masterView);

  String rewriteUpdateSql(String sql, UpdateItemMode updateItemMode, boolean reportItemNameExists);

}
