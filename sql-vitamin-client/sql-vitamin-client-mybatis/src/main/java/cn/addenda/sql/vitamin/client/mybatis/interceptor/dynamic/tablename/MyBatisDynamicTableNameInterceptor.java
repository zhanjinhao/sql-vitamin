package cn.addenda.sql.vitamin.client.mybatis.interceptor.dynamic.tablename;

import cn.addenda.sql.vitamin.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigDynamicTableName;
import cn.addenda.sql.vitamin.client.common.config.DynamicTableNameConfigUtils;
import cn.addenda.sql.vitamin.client.mybatis.interceptor.AbstractSqlVitaminMybatisInterceptor;
import cn.addenda.sql.vitamin.rewriter.dynamic.tablename.DynamicTableNameContext;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author addenda
 * @since 2023/6/8 22:58
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
public class MyBatisDynamicTableNameInterceptor extends AbstractSqlVitaminMybatisInterceptor {

  public MyBatisDynamicTableNameInterceptor(MsIdExtractHelper msIdExtractHelper) {
    super(msIdExtractHelper);
  }

  public MyBatisDynamicTableNameInterceptor() {
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // 获取 msId
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];
    String msId = ms.getId();
    ConfigDynamicTableName configDynamicTableName = msIdExtractHelper.extractConfigDynamicTableName(msId);
    if (configDynamicTableName == null) {
      return invocation.proceed();
    }

    try {
      DynamicTableNameConfigUtils.pushDynamicTableName(configDynamicTableName.propagation());
      DynamicTableNameConfigUtils.configDynamicTableName(configDynamicTableName);
      return invocation.proceed();
    } finally {
      DynamicTableNameContext.pop();
    }
  }

}
