package cn.addenda.sql.vitamins.rewriter.baseentity;

import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/5/12 17:56
 */
public abstract class ThreadLocalRemarkBaseEntitySource implements BaseEntitySource {

  private static final ThreadLocal<String> REMARK_THREADLOCAL = ThreadLocal.withInitial(() -> null);

  protected ThreadLocalRemarkBaseEntitySource() {
  }

  public static void setRemark(String remark) {
    REMARK_THREADLOCAL.set(remark);
  }

  public static void clearRemark() {
    REMARK_THREADLOCAL.remove();
  }

  @Override
  public String getRemark() {
    return REMARK_THREADLOCAL.get();
  }

  public static <T> T execute(String remark, Supplier<T> supplier) {
    setRemark(remark);
    try {
      return supplier.get();
    } finally {
      clearRemark();
    }
  }

  public static void execute(String remark, Runnable runnable) {
    setRemark(remark);
    try {
      runnable.run();
    } finally {
      clearRemark();
    }
  }

}
