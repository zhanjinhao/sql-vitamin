package cn.addenda.sql.vitamins.rewriter.dynamic.item;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlInterceptor;
import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneException;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.Item;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.List;

/**
 * 配置了拦截器 且 DynamicItemContext配置了条件 才会执行。
 *
 * @author addenda
 * @since 2023/4/30 16:30
 */
@Slf4j
public class DynamicItemSqlInterceptor extends AbstractSqlInterceptor {

  private final DynamicItemRewriter dynamicItemRewriter;

  public DynamicItemSqlInterceptor(boolean removeEnter, DynamicItemRewriter dynamicItemRewriter) {
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

    logDebug("Dynamic Item, before sql: [{}].", sql);
    sql = doRewrite(sql, dynamicItemConfigList);
    logDebug("Dynamic Item, after sql: [{}].", sql);
    return sql;
  }

  private String doRewrite(String sql, List<DynamicItemConfig> dynamicItemConfigList) {
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

      try {
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
          throw new DynamicSQLException(msg);
        }
      } catch (DynamicSQLException dynamicSQLException) {
        throw dynamicSQLException;
      } catch (Throwable throwable) {
        String msg = String.format("增加动态属性时出错，SQL：[%s]。", removeEnter(sql));
        throw new TombstoneException(msg, ExceptionUtil.unwrapThrowable(throwable));
      }
    }

    return newSql;
  }

  @Override
  public int order() {
    return MAX / 2 - 61000;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
