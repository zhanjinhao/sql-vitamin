package cn.addenda.sql.vitamins.rewriter.dynamic.item;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.Item;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 配置了拦截器 且 DynamicItemContext配置了条件 才会执行。
 *
 * @author addenda
 * @since 2023/4/30 16:30
 */
@Slf4j
public class DynamicItemSqlRewriter extends AbstractSqlRewriter {

  private final DynamicItemRewriter dynamicItemRewriter;

  public DynamicItemSqlRewriter(boolean removeEnter, DynamicItemRewriter dynamicItemRewriter) {
    super(removeEnter);
    if (dynamicItemRewriter == null) {
      throw new DynamicSQLException("`dynamicItemRewriter` can not be null!");
    }
    this.dynamicItemRewriter = dynamicItemRewriter;
  }

  @Override
  public String rewrite(String sql) {
    if (!DynamicItemContext.contextActive()) {
      return sql;
    }

    List<DynamicItemConfig> dynamicItemConfigList = DynamicItemContext.getDynamicItemConfigList();
    if (dynamicItemConfigList == null || dynamicItemConfigList.isEmpty()) {
      return sql;
    }

    log.debug("Dynamic Item, before sql rewriting: [{}].", removeEnter(sql));
    String newSql;
    try {
      newSql = doProcess(removeEnter(sql), dynamicItemConfigList);
    } catch (Throwable throwable) {
      String msg = String.format("拼装动态SQL时出错，SQL：[%s]，itemList：[%s]。", removeEnter(sql), dynamicItemConfigList);
      throw new DynamicSQLException(msg, ExceptionUtil.unwrapThrowable(throwable));
    }

    log.debug("Dynamic Item, after sql rewriting: [{}].", newSql);
    return newSql;
  }

  private String doProcess(String sql, List<DynamicItemConfig> dynamicItemConfigList) {
    String newSql = sql;

    for (DynamicItemConfig dynamicItemConfig : dynamicItemConfigList) {
      DynamicItemOperation dynamicItemOperation = dynamicItemConfig.getDynamicItemOperation();
      String tableName = dynamicItemConfig.getTableName();
      if (DynamicItemContext.ALL_TABLE.equals(tableName)) {
        tableName = null;
      }
      String itemName = dynamicItemConfig.getItemName();
      Object itemValue = dynamicItemConfig.getItemValue();
      InsertAddSelectItemMode insertAddSelectItemMode = dynamicItemConfig.getInsertAddSelectItemMode();
      Boolean duplicateKeyUpdate = dynamicItemConfig.getDuplicateKeyUpdate();
      UpdateItemMode updateItemMode = dynamicItemConfig.getUpdateItemMode();

      if (DynamicItemOperation.INSERT_ADD_ITEM == dynamicItemOperation) {
        if (JdbcSQLUtils.isInsert(newSql)) {
          newSql = dynamicItemRewriter.insertAddItem(newSql, tableName,
              new Item(itemName, itemValue), insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
        }
      } else if (DynamicItemOperation.UPDATE_ADD_ITEM == dynamicItemOperation) {
        if (JdbcSQLUtils.isUpdate(newSql)) {
          newSql = dynamicItemRewriter.updateAddItem(newSql, tableName, new Item(itemName, itemValue), updateItemMode);
        }
      } else {
        String msg = String.format("不支持的操作类型：[%s]，SQL：[%s]。", dynamicItemOperation, removeEnter(sql));
        throw new UnsupportedOperationException(msg);
      }
    }

    return newSql;
  }

  @Override
  public int order() {
    return MAX / 2 - 60000;
  }

}
