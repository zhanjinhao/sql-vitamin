package cn.addenda.sql.vitamin.client.spring.aop;

/**
 * @author addenda
 * @since 2023/6/14 20:04
 */
public interface NamedConfigurer {

  String DEFAULT = "DEFAULT";

  default String getName() {
    return DEFAULT;
  }

}
