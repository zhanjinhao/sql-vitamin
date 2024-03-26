package cn.addenda.sql.vitamins.client.mybatis.interceptor.dynamicsql;

import cn.addenda.sql.vitamins.client.common.ConfigContextUtils;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamins.client.mybatis.interceptor.AbstractSqlVitaminsMybatisInterceptor;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicSQLContext;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicSQLException;
import cn.addenda.sql.vitamins.rewriter.pojo.Binary;
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

import java.lang.reflect.Constructor;
import java.time.ZoneId;
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
public class MyBatisDynamicSQLInterceptor extends AbstractSqlVitaminsMybatisInterceptor {

  @Setter
  private DataConvertorRegistry dataConvertorRegistry;

  @Setter
  private ZoneId zoneId;

  public MyBatisDynamicSQLInterceptor() {
    super();
  }

  public MyBatisDynamicSQLInterceptor(MsIdExtractHelper msIdExtractHelper) {
    super(msIdExtractHelper);
  }

  public MyBatisDynamicSQLInterceptor(MsIdExtractHelper msIdExtractHelper, DataConvertorRegistry dataConvertorRegistry, ZoneId zoneId) {
    super(msIdExtractHelper);
    this.dataConvertorRegistry = dataConvertorRegistry;
    this.zoneId = zoneId;
  }

  public MyBatisDynamicSQLInterceptor(MsIdExtractHelper msIdExtractHelper, DataConvertorRegistry dataConvertorRegistry) {
    super(msIdExtractHelper);
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

  public MyBatisDynamicSQLInterceptor(DataConvertorRegistry dataConvertorRegistry, ZoneId zoneId) {
    this.dataConvertorRegistry = dataConvertorRegistry;
    this.zoneId = zoneId;
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Binary<String, Propagation> binary = extract(invocation);
    String msId = binary.getF1();
    Propagation propagation = binary.getF2();
    ConfigContextUtils.pushDynamicSQL(propagation);

    try {
      ConfigContextUtils.configDynamicSQL(propagation, dataConvertorRegistry,
        msIdExtractHelper.extractDynamicConditions(msId),
        msIdExtractHelper.extractConfigJoinUseSubQuery(msId),
        msIdExtractHelper.extractDynamicItems(msId),
        msIdExtractHelper.extractConfigDupThenNew(msId),
        msIdExtractHelper.extractConfigDuplicateKeyUpdate(msId),
        msIdExtractHelper.extractConfigUpdateItemMode(msId),
        msIdExtractHelper.extractConfigInsertSelectAddItemMode(msId));
      return invocation.proceed();
    } finally {
      DynamicSQLContext.pop();
    }
  }

  @Override
  public void setProperties(Properties properties) {
    super.setProperties(properties);
    String aDataConvertorRegistry = (String) properties.get("dataConvertorRegistry");
    String aZoneId = (String) properties.get("zoneId");
    if (aZoneId != null && aZoneId.length() != 0) {
      try {
        zoneId = ZoneId.of(aZoneId);
      } catch (Exception e) {
        String msg = String.format("无法识别的ZoneId：[%s]。", aZoneId);
        throw new DynamicSQLException(msg);
      }
    } else {
      zoneId = ZoneId.systemDefault();
    }

    if (aDataConvertorRegistry != null) {
      Class<? extends DataConvertorRegistry> aClass;
      try {
        aClass = (Class<? extends DataConvertorRegistry>) Class.forName(aDataConvertorRegistry);
      } catch (Exception e) {
        String msg = String.format("%s初始化失败：[%s]。", DataConvertorRegistry.class.getName(), aDataConvertorRegistry);
        throw new DynamicSQLException(msg, e);
      }

      try {
        Constructor<? extends DataConvertorRegistry> constructor = aClass.getConstructor(ZoneId.class);
        this.dataConvertorRegistry = constructor.newInstance(zoneId);
      } catch (Exception e) {
        String msg = String.format("%s初始化失败：[%s]。", DataConvertorRegistry.class.getName(), aDataConvertorRegistry);
        throw new DynamicSQLException(msg);
      }
    } else {
      this.dataConvertorRegistry = new DefaultDataConvertorRegistry(zoneId);
    }
  }

}
