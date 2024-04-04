package cn.addenda.sql.vitamins.rewriter.dynamic.tablename;

import cn.addenda.sql.vitamins.rewriter.AbstractSqlRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 配置了拦截器 且 DynamicTableNameContext配置了条件 才会执行。
 *
 * @author addenda
 * @since 2023/4/30 16:30
 */
@Slf4j
public class DynamicTableNameSqlRewriter extends AbstractSqlRewriter {

  private final DynamicTableNameRewriter dynamicTableNameRewriter;

  public DynamicTableNameSqlRewriter(boolean removeEnter, DynamicTableNameRewriter dynamicTableNameRewriter) {
    super(removeEnter);
    if (dynamicTableNameRewriter == null) {
      throw new DynamicSQLException("`dynamicTableNameRewriter` can not be null!");
    }
    this.dynamicTableNameRewriter = dynamicTableNameRewriter;
  }

  @Override
  public String rewrite(String sql) {
    if (!DynamicTableNameContext.contextActive()) {
      return sql;
    }

    List<DynamicTableNameConfig> dynamicTableNameConfigList = DynamicTableNameContext.getDynamicTableNameConfigList();
    if (dynamicTableNameConfigList == null || dynamicTableNameConfigList.isEmpty()) {
      return sql;
    }

    log.debug("Dynamic TableName, before sql rewriting: [{}].", removeEnter(sql));
    String newSql;
    try {
      newSql = doProcess(removeEnter(sql), dynamicTableNameConfigList);
    } catch (Throwable throwable) {
      String msg = String.format("拼装动态SQL时出错，SQL：[%s]，tableNameList：[%s]。", removeEnter(sql), dynamicTableNameConfigList);
      throw new DynamicSQLException(msg, ExceptionUtil.unwrapThrowable(throwable));
    }

    log.debug("Dynamic TableName, after sql rewriting: [{}].", newSql);
    return newSql;
  }

  private String doProcess(String sql, List<DynamicTableNameConfig> dynamicTableNameConfigList) {
    String newSql = sql;

    for (DynamicTableNameConfig dynamicTableNameConfig : dynamicTableNameConfigList) {
      newSql = dynamicTableNameRewriter.rename(
          newSql, dynamicTableNameConfig.getOriginalTableName(), dynamicTableNameConfig.getTargetTableName());
    }

    return newSql;
  }

  @Override
  public int order() {
    return MAX / 2 - 60000;
  }

}
