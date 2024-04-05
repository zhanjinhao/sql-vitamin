package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlInterceptor;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
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
  private final InsertAddSelectItemMode defaultInsertAddSelectItemMode;
  private final boolean defaultDuplicateKeyUpdate;
  private final UpdateItemMode defaultUpdateItemMode;

  public BaseEntitySqlInterceptor(
      boolean removeEnter, boolean disable,
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
    this.baseEntityRewriter = baseEntityRewriter;
    this.defaultInsertAddSelectItemMode = insertAddSelectItemMode;
    this.defaultDuplicateKeyUpdate = duplicateKeyUpdate;
    this.defaultUpdateItemMode = updateItemMode;
  }

  @Override
  public String rewrite(String sql) {
    Boolean disable = JdbcSQLUtils.getOrDefault(BaseEntityContext.getDisable(), defaultDisable);
    if (Boolean.TRUE.equals(disable)) {
      return sql;
    }

    logDebug("Base Entity, before sql: [{}].", sql);
    if (!BaseEntityContext.contextActive()) {
      try {
        BaseEntityContext.push(new BaseEntityConfig());
        sql = doRewrite(sql);
      } finally {
        BaseEntityContext.pop();
      }
    } else {
      sql = doRewrite(sql);
    }
    logDebug("Base Entity, after sql: [{}].", sql);

    return sql;
  }

  private String doRewrite(String sql) {
    String newSql = sql;
    try {
      if (JdbcSQLUtils.isSelect(newSql)) {
        newSql = baseEntityRewriter.rewriteSelectSql(newSql, BaseEntityContext.getMasterView());
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
      } else {
        String msg = String.format("仅支持select、update、delete、insert语句，当前SQL：[%s]。", removeEnter(sql));
        throw new BaseEntityException(msg);
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
