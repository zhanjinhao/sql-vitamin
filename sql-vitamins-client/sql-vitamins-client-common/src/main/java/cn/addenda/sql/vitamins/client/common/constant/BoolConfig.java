package cn.addenda.sql.vitamins.client.common.constant;

/**
 * @author addenda
 * @since 2024/3/27 21:38
 */
public enum BoolConfig {

  FALSE, TRUE, DEFAULT;

  public static Boolean toBoolean(BoolConfig boolConfig) {
    if (boolConfig == null || boolConfig == DEFAULT) {
      return null;
    }
    if (boolConfig == TRUE) {
      return true;
    }
    return false;
  }

}
