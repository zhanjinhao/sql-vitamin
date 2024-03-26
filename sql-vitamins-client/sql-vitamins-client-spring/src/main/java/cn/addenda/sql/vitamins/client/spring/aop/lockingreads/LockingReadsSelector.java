package cn.addenda.sql.vitamins.client.spring.aop.lockingreads;

import cn.addenda.sql.vitamins.client.spring.aop.AbstractSqlVitaminsAopModeImportSelector;
import cn.addenda.sql.vitamins.client.spring.aop.SqlVitaminsAopMode;
import cn.addenda.sql.vitamins.rewriter.lockingreads.LockingReadsException;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * @author addenda
 * @since 2022/9/29 13:50
 */
public class LockingReadsSelector extends AbstractSqlVitaminsAopModeImportSelector<EnableLockingReads> {

  @Override
  protected String[] selectImports(SqlVitaminsAopMode sqlVitaminsAopMode) {
    if (sqlVitaminsAopMode == SqlVitaminsAopMode.ONLY_CONFIG) {
      return new String[]{
        AutoProxyRegistrar.class.getName(),
        LockingReadsConfigConfiguration.class.getName()};
    } else if (sqlVitaminsAopMode == SqlVitaminsAopMode.PROXY_CONFIG) {
      return new String[]{
        AutoProxyRegistrar.class.getName(),
        LockingReadsConfigConfiguration.class.getName(),
        LockingReadsProxyConfiguration.class.getName()};
    }
    String msg = String.format("SqlVitaminsAopMode 只可选 ONLY_CONFIG 和 PROXY_CONFIG 两种，当前是：[%s]。", sqlVitaminsAopMode);
    throw new LockingReadsException(msg);
  }

}
