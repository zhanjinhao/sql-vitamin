package cn.addenda.sql.vitamin.rewriter.baseentity;

import cn.addenda.sql.vitamin.rewriter.AbstractSqlInterceptor;
import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamin.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 只要配置了拦截器就会执行SQL拦截。
 *
 * @author addenda
 * @since 2023/5/2 17:33
 */
@Slf4j
public class BaseEntitySqlInterceptor extends AbstractSqlInterceptor {

  private final BaseEntityRewriter baseEntityRewriter;
  private final boolean defaultDisable;
  private final boolean defaultSelectDisable;
  private final boolean defaultCompatibleMode;
  private final InsertAddSelectItemMode defaultInsertAddSelectItemMode;
  private final boolean defaultDuplicateKeyUpdate;
  private final UpdateItemMode defaultUpdateItemMode;

  public BaseEntitySqlInterceptor(
      boolean removeEnter, boolean disable, boolean selectDisable, boolean compatibleMode,
      BaseEntityRewriter baseEntityRewriter, InsertAddSelectItemMode insertAddSelectItemMode,
      boolean duplicateKeyUpdate, UpdateItemMode updateItemMode) {
    super(removeEnter);
    if (baseEntityRewriter == null) {
      throw new BaseEntityException("`baseEntityRewriter` can not be null!");
    }
    if (insertAddSelectItemMode == null) {
      throw new BaseEntityException("`insertAddSelectItemMode` can not be null!");
    }
    if (updateItemMode == null) {
      throw new BaseEntityException("`updateItemMode` can not be null!");
    }
    this.defaultDisable = disable;
    this.defaultSelectDisable = selectDisable;
    this.defaultCompatibleMode = compatibleMode;
    this.baseEntityRewriter = baseEntityRewriter;
    this.defaultInsertAddSelectItemMode = insertAddSelectItemMode;
    this.defaultDuplicateKeyUpdate = duplicateKeyUpdate;
    this.defaultUpdateItemMode = updateItemMode;
  }

  @Override
  public String rewrite(String sql) {

    logDebug("Base Entity, before sql: [{}].", sql);
    if (!BaseEntityContext.contextActive()) {
      if (!defaultDisable) {
        try {
          BaseEntityContext.push(new BaseEntityConfig());
          sql = doRewrite(sql);
        } finally {
          BaseEntityContext.pop();
        }
      }
    } else {
      Boolean disable = JdbcSQLUtils.getOrDefault(BaseEntityContext.getDisable(), defaultDisable);
      if (Boolean.FALSE.equals(disable)) {
        sql = doRewrite(sql);
      }
    }
    logDebug("Base Entity, after sql: [{}].", sql);

    return sql;
  }

  private String doRewrite(String sql) {
    String newSql = sql;
    try {
      if (JdbcSQLUtils.isSelect(newSql)) {
        Boolean selectDisable = JdbcSQLUtils.getOrDefault(BaseEntityContext.getSelectDisable(), defaultSelectDisable);
        if (Boolean.FALSE.equals(selectDisable)) {
          newSql = baseEntityRewriter.rewriteSelectSql(newSql, BaseEntityContext.getMasterView());
        }
      } else if (JdbcSQLUtils.isUpdate(newSql)) {
        UpdateItemMode updateItemMode =
            JdbcSQLUtils.getOrDefault(BaseEntityContext.getUpdateItemMode(), defaultUpdateItemMode);
        newSql = baseEntityRewriter.rewriteUpdateSql(newSql, updateItemMode);
      } else if (JdbcSQLUtils.isInsert(newSql)) {
        UpdateItemMode updateItemMode =
            JdbcSQLUtils.getOrDefault(BaseEntityContext.getUpdateItemMode(), defaultUpdateItemMode);
        Boolean duplicateKeyUpdate =
            JdbcSQLUtils.getOrDefault(BaseEntityContext.getDuplicateKeyUpdate(), defaultDuplicateKeyUpdate);
        InsertAddSelectItemMode insertAddSelectItemMode =
            JdbcSQLUtils.getOrDefault(BaseEntityContext.getInsertSelectAddItemMode(), defaultInsertAddSelectItemMode);
        newSql = baseEntityRewriter.rewriteInsertSql(newSql, insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
      } else if (JdbcSQLUtils.isDelete(newSql)) {
        // delete语法不会影响baseEntity，直接不处理
      } else {
        // 非CURD语句，有可能是修改数据，此时会影响baseEntity的功能。
        // 兼容模式跳过非CURD数据。非兼容模式抛异常阻止SQL执行。
        Boolean compatibleMode = JdbcSQLUtils.getOrDefault(BaseEntityContext.getCompatibleMode(), defaultCompatibleMode);
        String msg = String.format("仅支持select、update、delete、insert语句，当前SQL：[%s]。", removeEnter(sql));
        if (Boolean.TRUE.equals(compatibleMode)) {
          log.info(msg);
        } else {
          throw new BaseEntityException(msg);
        }
      }
    } catch (BaseEntityException baseEntityException) {
      throw baseEntityException;
    } catch (Throwable throwable) {
      String msg = String.format("物理删除改写为逻辑删除时出错，SQL：[%s]。", removeEnter(sql));
      throw new BaseEntityException(msg, ExceptionUtil.unwrapThrowable(throwable));
    }
    return newSql;
  }

  @Override
  public int order() {
    return MAX / 2 - 50000;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
