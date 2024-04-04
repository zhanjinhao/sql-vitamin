package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlRewriter;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.extern.slf4j.Slf4j;

/**
 * 只要配置了拦截器就会执行SQL拦截。
 *
 * @author addenda
 * @since 2023/5/2 17:33
 */
@Slf4j
public class BaseEntitySqlRewriter extends AbstractSqlRewriter {

  private final BaseEntityRewriter baseEntityRewriter;
  private final boolean defaultDisable;
  private final InsertAddSelectItemMode defaultInsertAddSelectItemMode;
  private final boolean defaultDuplicateKeyUpdate;
  private final UpdateItemMode defaultUpdateItemMode;

  public BaseEntitySqlRewriter(
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
    if (!BaseEntityContext.contextActive()) {
      return sql;
    }

    Boolean disable = JdbcSQLUtils.getOrDefault(BaseEntityContext.getDisable(), defaultDisable);
    if (Boolean.TRUE.equals(disable)) {
      return sql;
    }
    log.debug("Base Entity, before sql rewriting: [{}].", sql);
    try {
      if (JdbcSQLUtils.isSelect(sql)) {
        sql = baseEntityRewriter.rewriteSelectSql(sql, BaseEntityContext.getMasterView());
      } else if (JdbcSQLUtils.isUpdate(sql)) {
        UpdateItemMode updateItemMode =
            JdbcSQLUtils.getOrDefault(BaseEntityContext.getUpdateItemMode(), defaultUpdateItemMode);
        sql = baseEntityRewriter.rewriteUpdateSql(sql, updateItemMode);
      } else if (JdbcSQLUtils.isInsert(sql)) {
        UpdateItemMode updateItemMode =
            JdbcSQLUtils.getOrDefault(BaseEntityContext.getUpdateItemMode(), defaultUpdateItemMode);
        Boolean duplicateKeyUpdate =
            JdbcSQLUtils.getOrDefault(BaseEntityContext.getDuplicateKeyUpdate(), defaultDuplicateKeyUpdate);
        InsertAddSelectItemMode insertAddSelectItemMode =
            JdbcSQLUtils.getOrDefault(BaseEntityContext.getInsertSelectAddItemMode(), defaultInsertAddSelectItemMode);
        sql = baseEntityRewriter.rewriteInsertSql(sql, insertAddSelectItemMode, duplicateKeyUpdate, updateItemMode);
      } else {
        throw new BaseEntityException("仅支持select、update、delete、insert语句，当前SQL：" + sql + "。");
      }
    } catch (Throwable throwable) {
      throw new BaseEntityException("基础字段填充时出错，SQL：" + sql + "。", ExceptionUtil.unwrapThrowable(throwable));
    }
    log.debug("Base Entity, after sql rewriting: [{}].", sql);
    return sql;
  }

  @Override
  public int order() {
    return MAX / 2 - 50000;
  }
}
