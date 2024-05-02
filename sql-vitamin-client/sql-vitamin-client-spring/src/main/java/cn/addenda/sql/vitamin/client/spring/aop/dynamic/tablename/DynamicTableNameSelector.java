package cn.addenda.sql.vitamin.client.spring.aop.dynamic.tablename;

import cn.addenda.sql.vitamin.client.spring.aop.SqlVitaminAopMode;
import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminAopModeImportSelector;
import cn.addenda.sql.vitamin.rewriter.dynamic.DynamicSQLException;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * @author addenda
 * @since 2022/9/29 13:50
 */
public class DynamicTableNameSelector extends AbstractSqlVitaminAopModeImportSelector<EnableDynamicTableName> {

  @Override
  public String[] selectImports(SqlVitaminAopMode sqlVitaminAopMode) {
    if (sqlVitaminAopMode == SqlVitaminAopMode.ONLY_CONFIG) {
      return new String[]{
          AutoProxyRegistrar.class.getName(),
          DynamicTableNameConfigConfiguration.class.getName()};
    } else if (sqlVitaminAopMode == SqlVitaminAopMode.PROXY_CONFIG) {
      return new String[]{
          AutoProxyRegistrar.class.getName(),
          DynamicTableNameConfigConfiguration.class.getName(),
          DynamicTableNameProxyConfiguration.class.getName()};
    }
    String msg = String.format("SqlVitaminAopMode 只可选 ONLY_CONFIG 和 PROXY_CONFIG 两种，当前是：[%s]。", sqlVitaminAopMode);
    throw new DynamicSQLException(msg);
  }

}
