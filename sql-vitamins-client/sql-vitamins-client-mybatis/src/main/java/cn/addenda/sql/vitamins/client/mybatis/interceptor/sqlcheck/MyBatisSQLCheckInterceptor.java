package cn.addenda.sql.vitamins.client.mybatis.interceptor.sqlcheck;

import cn.addenda.sql.vitamins.client.common.ConfigContextUtils;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamins.client.mybatis.interceptor.AbstractSqlVitaminsMybatisInterceptor;
import cn.addenda.sql.vitamins.rewriter.pojo.Binary;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SQLCheckContext;
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
public class MyBatisSQLCheckInterceptor extends AbstractSqlVitaminsMybatisInterceptor {

  public MyBatisSQLCheckInterceptor(MsIdExtractHelper msIdExtractHelper) {
    super(msIdExtractHelper);
  }

  public MyBatisSQLCheckInterceptor() {
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Binary<String, Propagation> binary = extract(invocation);
    String msId = binary.getF1();
    Propagation propagation = binary.getF2();

    ConfigContextUtils.pushSQLCheck(propagation);
    try {
      ConfigContextUtils.configSQLCheck(propagation,
        msIdExtractHelper.extractUnCheckAllColumn(msId),
        msIdExtractHelper.extractUnCheckExactIdentifier(msId),
        msIdExtractHelper.extractUnCheckDmlCondition(msId));
      return invocation.proceed();
    } finally {
      SQLCheckContext.pop();
    }
  }

}
