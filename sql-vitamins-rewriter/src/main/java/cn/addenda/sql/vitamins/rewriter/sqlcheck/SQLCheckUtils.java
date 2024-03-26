package cn.addenda.sql.vitamins.rewriter.sqlcheck;

import cn.addenda.sql.vitamins.rewriter.function.TRunnable;
import cn.addenda.sql.vitamins.rewriter.function.TSupplier;
import cn.addenda.sql.vitamins.rewriter.util.ExceptionUtil;

/**
 * @author addenda
 * @since 2023/5/7 15:55
 */
public class SQLCheckUtils {

  private SQLCheckUtils() {
  }

  public static <T> T unCheckAllColumn(TSupplier<T> tSupplier) {
    SQLCheckContext.push();
    try {
      SQLCheckContext.setCheckAllColumn(false);
      return tSupplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, SQLCheckException.class);
      return null;
    } finally {
      SQLCheckContext.pop();
    }
  }

  public static <T> T unCheckExactIdentifier(TSupplier<T> tSupplier) {
    SQLCheckContext.push();
    try {
      SQLCheckContext.setCheckExactIdentifier(false);
      return tSupplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, SQLCheckException.class);
      return null;
    } finally {
      SQLCheckContext.pop();
    }
  }

  public static <T> T unCheckDmlCondition(TSupplier<T> tSupplier) {
    SQLCheckContext.push();
    try {
      SQLCheckContext.setCheckDmlCondition(false);
      return tSupplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, SQLCheckException.class);
      return null;
    } finally {
      SQLCheckContext.pop();
    }
  }

  public static <T> T check(TSupplier<T> tSupplier, boolean... unChecks) {
    SQLCheckContext.push();
    try {
      if (unChecks != null) {
        if (unChecks.length > 0) {
          SQLCheckContext.setCheckAllColumn(unChecks[0]);
        }
        if (unChecks.length > 1) {
          SQLCheckContext.setCheckExactIdentifier(unChecks[1]);
        }
        if (unChecks.length > 2) {
          SQLCheckContext.setCheckDmlCondition(unChecks[2]);
        }
      }
      return tSupplier.get();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, SQLCheckException.class);
      return null;
    } finally {
      SQLCheckContext.pop();
    }
  }

  public static void unCheckAllColumn(TRunnable tRunnable) {
    SQLCheckContext.push();
    try {
      SQLCheckContext.setCheckAllColumn(false);
      tRunnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, SQLCheckException.class);
    } finally {
      SQLCheckContext.pop();
    }
  }

  public static void unCheckExactIdentifier(TRunnable tRunnable) {
    SQLCheckContext.push();
    try {
      SQLCheckContext.setCheckExactIdentifier(false);
      tRunnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, SQLCheckException.class);
    } finally {
      SQLCheckContext.pop();
    }
  }

  public static void unCheckDmlCondition(TRunnable tRunnable) {
    SQLCheckContext.push();
    try {
      SQLCheckContext.setCheckDmlCondition(false);
      tRunnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, SQLCheckException.class);
    } finally {
      SQLCheckContext.pop();
    }
  }

  public static void check(TRunnable tRunnable, boolean... unChecks) {
    SQLCheckContext.push();
    try {
      if (unChecks != null) {
        if (unChecks.length > 0) {
          SQLCheckContext.setCheckAllColumn(unChecks[0]);
        }
        if (unChecks.length > 1) {
          SQLCheckContext.setCheckExactIdentifier(unChecks[1]);
        }
        if (unChecks.length > 2) {
          SQLCheckContext.setCheckDmlCondition(unChecks[2]);
        }
      }
      tRunnable.run();
    } catch (Throwable e) {
      ExceptionUtil.reportAsRuntimeException(e, SQLCheckException.class);
    } finally {
      SQLCheckContext.pop();
    }
  }

}
