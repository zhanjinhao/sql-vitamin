package cn.addenda.sql.vitamins.client.common.config;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigLockingRead;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.rewriter.lockingread.LockingReadConfig;
import cn.addenda.sql.vitamins.rewriter.lockingread.LockingReadContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author addenda
 * @since 2023/6/11 16:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LockingReadConfigUtils {

  public static void configLockingRead(Propagation propagation, LockingReadConfig lockingReadConfig) {
    Propagation.assertNotNull(propagation);
    String lock = lockingReadConfig.getLock();
    if (lock != null) {
      Propagation.configWithPropagation(propagation, lock,
          LockingReadContext::setLock, LockingReadContext::getLock);
    }
  }

  public static void configLockingRead(ConfigLockingRead configLockingRead) {
    Propagation propagation = configLockingRead.propagation();
    configLockingRead(propagation, LockingReadConfig.of(configLockingRead.value()));
  }

  public static void pushLockingRead(Propagation propagation) {
    Propagation.assertNotNull(propagation);
    // NEW 压入新的
    if (propagation == Propagation.NEW || !LockingReadContext.contextActive()) {
      LockingReadContext.push(new LockingReadConfig());
    }
    // MERGE_* 压入带参数的
    else {
      LockingReadConfig lockingReadConfig = LockingReadContext.peek();
      LockingReadContext.push(new LockingReadConfig(lockingReadConfig));
    }
  }

}
