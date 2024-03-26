package cn.addenda.sql.vitamins.rewriter.function;

/**
 * @author addenda
 * @since 2023/6/4 15:11
 */
@FunctionalInterface
public interface TRunnable {

  void run() throws Throwable;

  default <R> TSupplier<R> toTSupplier() {
    return new TSupplier<R>() {
      @Override
      public R get() throws Throwable {
        run();
        return null;
      }
    };
  }

}
