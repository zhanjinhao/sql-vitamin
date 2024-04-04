package cn.addenda.sql.vitamins.rewriter;

/**
 * @author addenda
 * @since 2024/3/31 16:03
 */
public interface SqlInterceptor {

  int MAX = Integer.MAX_VALUE;

  String rewrite(String sql);

  default int order() {
    return MAX / 2 - 60000;
  }

}
