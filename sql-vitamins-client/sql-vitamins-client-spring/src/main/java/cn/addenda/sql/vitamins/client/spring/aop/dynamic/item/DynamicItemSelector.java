package cn.addenda.sql.vitamins.client.spring.aop.dynamic.item;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsAopModeImportSelector;
import cn.addenda.sql.vitamins.client.spring.aop.SqlVitaminsAopMode;
import cn.addenda.sql.vitamins.rewriter.dynamic.DynamicSQLException;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * @author addenda
 * @since 2022/9/29 13:50
 */
public class DynamicItemSelector extends AbstractSqlVitaminsAopModeImportSelector<EnableDynamicItem> {

  @Override
  public String[] selectImports(SqlVitaminsAopMode sqlVitaminsAopMode) {
    if (sqlVitaminsAopMode == SqlVitaminsAopMode.ONLY_CONFIG) {
      return new String[]{
        AutoProxyRegistrar.class.getName(),
        DynamicItemConfigConfiguration.class.getName()};
    } else if (sqlVitaminsAopMode == SqlVitaminsAopMode.PROXY_CONFIG) {
      return new String[]{
        AutoProxyRegistrar.class.getName(),
        DynamicItemConfigConfiguration.class.getName(),
        DynamicItemProxyConfiguration.class.getName()};
    }
    String msg = String.format("SqlVitaminsAopMode 只可选 ONLY_CONFIG 和 PROXY_CONFIG 两种，当前是：[%s]。", sqlVitaminsAopMode);
    throw new DynamicSQLException(msg);
  }

}
