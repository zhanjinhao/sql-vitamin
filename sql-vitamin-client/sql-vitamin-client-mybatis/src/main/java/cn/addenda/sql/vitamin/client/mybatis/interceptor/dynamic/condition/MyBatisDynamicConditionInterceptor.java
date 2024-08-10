package cn.addenda.sql.vitamin.client.mybatis.interceptor.dynamic.condition;

import cn.addenda.sql.vitamin.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigDynamicCondition;
import cn.addenda.sql.vitamin.client.common.config.DynamicConditionConfigUtils;
import cn.addenda.sql.vitamin.client.mybatis.interceptor.AbstractSqlVitaminMybatisInterceptor;
import cn.addenda.sql.vitamin.rewriter.dynamic.condition.DynamicConditionContext;
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
public class MyBatisDynamicConditionInterceptor extends AbstractSqlVitaminMybatisInterceptor {

  public MyBatisDynamicConditionInterceptor(MsIdExtractHelper msIdExtractHelper) {
    super(msIdExtractHelper);
  }

  public MyBatisDynamicConditionInterceptor() {
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // 获取 msId
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];
    String msId = ms.getId();
    ConfigDynamicCondition configDynamicCondition = msIdExtractHelper.extractConfigDynamicCondition(msId);
    if (configDynamicCondition == null) {
      return invocation.proceed();
    }

    try {
      DynamicConditionConfigUtils.pushDynamicCondition(configDynamicCondition.propagation());
      DynamicConditionConfigUtils.configDynamicCondition(configDynamicCondition);
      return invocation.proceed();
    } finally {
      DynamicConditionContext.pop();
    }
  }

}
