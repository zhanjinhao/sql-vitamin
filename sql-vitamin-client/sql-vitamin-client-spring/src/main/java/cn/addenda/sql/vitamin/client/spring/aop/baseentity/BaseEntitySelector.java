package cn.addenda.sql.vitamin.client.spring.aop.baseentity;

import cn.addenda.sql.vitamin.client.spring.aop.AbstractSqlVitaminAopModeImportSelector;
import cn.addenda.sql.vitamin.client.spring.aop.SqlVitaminAopMode;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityException;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * @author addenda
 * @since 2022/9/29 13:50
 */
public class BaseEntitySelector extends AbstractSqlVitaminAopModeImportSelector<EnableBaseEntity> {

  @Override
  public String[] selectImports(SqlVitaminAopMode sqlVitaminAopMode) {
    if (sqlVitaminAopMode == SqlVitaminAopMode.ONLY_CONFIG) {
      return new String[]{
        AutoProxyRegistrar.class.getName(),
        BaseEntityConfigConfiguration.class.getName()};
    } else if (sqlVitaminAopMode == SqlVitaminAopMode.PROXY_CONFIG) {
      return new String[]{
        AutoProxyRegistrar.class.getName(),
        BaseEntityConfigConfiguration.class.getName(),
        BaseEntityProxyConfiguration.class.getName()};
    }
    String msg = String.format("%s 只可选 ONLY_CONFIG 和 PROXY_CONFIG 两种，当前是：[%s]。", SqlVitaminAopMode.class.getName(), sqlVitaminAopMode);
    throw new BaseEntityException(msg);
  }

}
