package cn.addenda.sql.vitamin.client.mybatis.interceptor.tombstone;

import cn.addenda.sql.vitamin.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamin.client.mybatis.interceptor.AbstractSqlVitaminMybatisInterceptor;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigTombstone;
import cn.addenda.sql.vitamin.client.common.config.TombstoneConfigUtils;
import cn.addenda.sql.vitamin.rewriter.tombstone.TombstoneContext;
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
 * @since 2023/6/10 14:06
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
public class MyBatisTombstoneInterceptor extends AbstractSqlVitaminMybatisInterceptor {

  public MyBatisTombstoneInterceptor(MsIdExtractHelper msIdExtractHelper) {
    super(msIdExtractHelper);
  }

  public MyBatisTombstoneInterceptor() {
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // 获取 msId
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];
    String msId = ms.getId();
    ConfigTombstone configTombstone = msIdExtractHelper.extractConfigTombstone(msId);
    if (configTombstone == null) {
      return invocation.proceed();
    }

    try {
      TombstoneConfigUtils.pushTombstone(configTombstone.propagation());
      TombstoneConfigUtils.configTombstone(configTombstone);
      return invocation.proceed();
    } finally {
      TombstoneContext.pop();
    }
  }

}
