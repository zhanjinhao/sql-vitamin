package cn.addenda.sql.vitamin.rewriter.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author addenda
 * @since 2023/5/3 17:04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcSQLUtils {

  public static boolean isSelect(String sql) {
    return hasPrefix(sql, "select");
  }

  public static boolean isUpdate(String sql) {
    return hasPrefix(sql, "update");
  }

  public static boolean isDelete(String sql) {
    return hasPrefix(sql, "delete");
  }

  public static boolean isInsert(String sql) {
    return hasPrefix(sql, "insert");
  }

  public static boolean hasPrefix(String sql, String base) {
    int length = sql.length();
    int st = 0;
    while ((st < length) && (sql.charAt(st) <= ' ')) {
      st++;
    }

    int baseLength = base.length();

    if (length - st < baseLength) {
      return false;
    }

    for (int i = 0; i < baseLength; i++) {
      if (Character.toLowerCase(base.charAt(i)) != Character.toLowerCase(sql.charAt(st + i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param excluded excluded里的表一定返回false；
   * @param included included为null时，!excluded的表返回true；included不为null时，!excluded&included的表返回true。
   */
  public static boolean include(
      String tableName, List<String> included, List<String> excluded) {
    if (excluded != null) {
      for (String unContain : excluded) {
        if (unContain.equalsIgnoreCase(tableName)) {
          return false;
        }
      }
    }
    if (included == null) {
      return true;
    }
    for (String contain : included) {
      if (contain.equalsIgnoreCase(tableName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * <ul>
   *   <li>column : column</li>
   *   <li>table.column : column</li>
   *   <li>schema.table.column : column</li>
   * </ul>
   */
  public static String extractColumnName(String value) {
    int i = value.lastIndexOf(".");
    if (i == -1) {
      return value;
    }
    return value.substring(i + 1);
  }

  /**
   * <ul>
   *   <li>column : null</li>
   *   <li>table.column : table</li>
   *   <li>schema.table.column : table</li>
   * </ul>
   */
  public static String extractColumnOwner(String value) {
    int i = value.indexOf(".");
    if (i == -1) {
      return null;
    }
    int j = value.lastIndexOf(".");
    if (i == j) {
      return value.substring(0, i);
    }
    return value.substring(i + 1, j);
  }

  public static boolean isEmpty(CharSequence value) {
    return value == null || value.length() == 0;
  }

  public static <T> T getOrDefault(T get, T _default) {
    if (get == null) {
      return _default;
    }
    return get;
  }

}
