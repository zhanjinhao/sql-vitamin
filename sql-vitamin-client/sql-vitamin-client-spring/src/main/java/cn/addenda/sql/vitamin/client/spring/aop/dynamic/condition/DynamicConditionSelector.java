package cn.addenda.sql.vitamin.client.spring.aop.dynamic.condition;

import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminAopModeImportSelector;
import cn.addenda.sql.vitamin.client.spring.aop.SqlVitaminAopMode;
import cn.addenda.sql.vitamin.rewriter.dynamic.DynamicSQLException;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * @author addenda
 * @since 2022/9/29 13:50
 */
public class DynamicConditionSelector extends AbstractSqlVitaminAopModeImportSelector<EnableDynamicCondition> {

  @Override
  public String[] selectImports(SqlVitaminAopMode sqlVitaminAopMode) {
    if (sqlVitaminAopMode == SqlVitaminAopMode.ONLY_CONFIG) {
      return new String[]{
          AutoProxyRegistrar.class.getName(),
          DynamicConditionConfigConfiguration.class.getName()};
    } else if (sqlVitaminAopMode == SqlVitaminAopMode.PROXY_CONFIG) {
      return new String[]{
          AutoProxyRegistrar.class.getName(),
          DynamicConditionConfigConfiguration.class.getName(),
          DynamicConditionProxyConfiguration.class.getName()};
    }
    String msg = String.format("SqlVitaminAopMode 只可选 ONLY_CONFIG 和 PROXY_CONFIG 两种，当前是：[%s]。", sqlVitaminAopMode);
    throw new DynamicSQLException(msg);
  }

}
