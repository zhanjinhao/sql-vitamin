package cn.addenda.sql.vitamins.client.mybatis.interceptor.lockingread;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigLockingRead;
import cn.addenda.sql.vitamins.client.common.config.LockingReadConfigUtils;
import cn.addenda.sql.vitamins.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamins.client.mybatis.interceptor.AbstractSqlVitaminsMybatisInterceptor;
import cn.addenda.sql.vitamins.rewriter.lockingread.LockingReadContext;
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
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MyBatisLockingReadInterceptor extends AbstractSqlVitaminsMybatisInterceptor {

  public MyBatisLockingReadInterceptor(MsIdExtractHelper msIdExtractHelper) {
    super(msIdExtractHelper);
  }

  public MyBatisLockingReadInterceptor() {
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];
    String msId = ms.getId();

    try {
      ConfigLockingRead configLockingRead = msIdExtractHelper.extractConfigLock(msId);
      LockingReadConfigUtils.pushLockingRead(configLockingRead.propagation());
      LockingReadConfigUtils.configLockingRead(configLockingRead);
      return invocation.proceed();
    } finally {
      LockingReadContext.pop();
    }
  }

}
