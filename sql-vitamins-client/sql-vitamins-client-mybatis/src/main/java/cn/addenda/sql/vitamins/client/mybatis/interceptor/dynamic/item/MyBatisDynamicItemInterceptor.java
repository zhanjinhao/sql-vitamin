package cn.addenda.sql.vitamins.client.mybatis.interceptor.dynamic.item;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigDynamicItem;
import cn.addenda.sql.vitamins.client.common.config.DynamicItemConfigUtils;
import cn.addenda.sql.vitamins.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamins.client.mybatis.interceptor.AbstractSqlVitaminsMybatisInterceptor;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemContext;
import lombok.Setter;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.Properties;

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
public class MyBatisDynamicItemInterceptor extends AbstractSqlVitaminsMybatisInterceptor {

  @Setter
  private DataConvertorRegistry dataConvertorRegistry;

  public MyBatisDynamicItemInterceptor(MsIdExtractHelper msIdExtractHelper) {
    super(msIdExtractHelper);
  }

  public MyBatisDynamicItemInterceptor(MsIdExtractHelper msIdExtractHelper, DataConvertorRegistry dataConvertorRegistry) {
    super(msIdExtractHelper);
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

  public MyBatisDynamicItemInterceptor() {
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // 获取 msId
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];
    String msId = ms.getId();

    try {
      ConfigDynamicItem configDynamicItem = msIdExtractHelper.extractConfigDynamicItem(msId);
      DynamicItemConfigUtils.pushDynamicItem(configDynamicItem.propagation());
      DynamicItemConfigUtils.configDynamicItem(configDynamicItem, dataConvertorRegistry);
      return invocation.proceed();
    } finally {
      DynamicItemContext.pop();
    }
  }

  @Override
  public void setProperties(Properties properties) {
    super.setProperties(properties);
    String aDataConvertorRegistry = (String) properties.get("dataConvertorRegistry");
    if (aDataConvertorRegistry == null) {
      throw new DynamicSQLException("`dataConvertorRegistry` can not be null!");
    }
    Class<? extends DataConvertorRegistry> aClass;
    try {
      aClass = (Class<? extends DataConvertorRegistry>) Class.forName(aDataConvertorRegistry);
      Method getInstance = aClass.getMethod("getInstance");
      this.dataConvertorRegistry = (DataConvertorRegistry) getInstance.invoke(null);
    } catch (Exception e) {
      String msg = String.format("%s初始化失败：[%s]。", DataConvertorRegistry.class.getName(), aDataConvertorRegistry);
      throw new DynamicSQLException(msg, e);
    }
  }

}
