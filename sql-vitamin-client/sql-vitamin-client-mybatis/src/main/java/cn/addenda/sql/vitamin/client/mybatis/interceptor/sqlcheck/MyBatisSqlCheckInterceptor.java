package cn.addenda.sql.vitamin.client.mybatis.interceptor.sqlcheck;

import cn.addenda.sql.vitamin.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigSqlCheck;
import cn.addenda.sql.vitamin.client.common.config.SqlCheckConfigUtils;
import cn.addenda.sql.vitamin.client.mybatis.interceptor.AbstractSqlVitaminMybatisInterceptor;
import cn.addenda.sql.vitamin.rewriter.sqlcheck.SqlCheckContext;
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
public class MyBatisSqlCheckInterceptor extends AbstractSqlVitaminMybatisInterceptor {

  public MyBatisSqlCheckInterceptor(MsIdExtractHelper msIdExtractHelper) {
    super(msIdExtractHelper);
  }

  public MyBatisSqlCheckInterceptor() {
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];
    String msId = ms.getId();
    try {
      ConfigSqlCheck configSqlCheck = msIdExtractHelper.extractConfigSqlCheck(msId);
      SqlCheckConfigUtils.pushSqlCheck(configSqlCheck.propagation());
      SqlCheckConfigUtils.configSqlCheck(configSqlCheck);
      return invocation.proceed();
    } finally {
      SqlCheckContext.pop();
    }
  }

}
