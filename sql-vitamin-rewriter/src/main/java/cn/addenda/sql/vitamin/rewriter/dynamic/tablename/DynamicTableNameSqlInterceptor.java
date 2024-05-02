package cn.addenda.sql.vitamin.rewriter.dynamic.tablename;

import cn.addenda.sql.vitamin.rewriter.AbstractSqlInterceptor;
import cn.addenda.sql.vitamin.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamin.rewriter.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.List;

/**
 * 配置了拦截器 且 DynamicTableNameContext配置了条件 才会执行。
 *
 * @author addenda
 * @since 2023/4/30 16:30
 */
@Slf4j
public class DynamicTableNameSqlInterceptor extends AbstractSqlInterceptor {

  private final DynamicTableNameRewriter dynamicTableNameRewriter;

  public DynamicTableNameSqlInterceptor(boolean removeEnter, DynamicTableNameRewriter dynamicTableNameRewriter) {
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

    logDebug("Dynamic TableName, before sql: [{}].", sql);
    sql = doRewrite(sql, dynamicTableNameConfigList);
    logDebug("Dynamic TableName, after sql: [{}].", sql);

    return sql;
  }

  private String doRewrite(String sql, List<DynamicTableNameConfig> dynamicTableNameConfigList) {
    try {
      String newSql = sql;
      for (DynamicTableNameConfig dynamicTableNameConfig : dynamicTableNameConfigList) {
        // 如果SQL里没有original表名，则不会替换
        newSql = dynamicTableNameRewriter.rename(
            newSql, dynamicTableNameConfig.getOriginalTableName(), dynamicTableNameConfig.getTargetTableName());
      }

      return newSql;
    } catch (Throwable throwable) {
      String msg = String.format("动态替换表名时出错，SQL：[%s]，tableNameList：[%s]。", removeEnter(sql), dynamicTableNameConfigList);
      throw new DynamicSQLException(msg, ExceptionUtil.unwrapThrowable(throwable));
    }
  }

  @Override
  public int order() {
    return MAX / 2 - 60000;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
