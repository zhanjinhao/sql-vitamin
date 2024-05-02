package cn.addenda.sql.vitamin.rewriter.sqlcheck;

import lombok.*;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author addenda
 * @since 2023/5/7 15:55
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlCheckContext {

  private static final ThreadLocal<Deque<SqlCheckConfig>> SQL_CHECK_TL = ThreadLocal.withInitial(() -> null);

  public static void setCheckAllColumn(boolean check) {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig sqlCheckConfig = sqlCheckConfigDeque.peek();
    sqlCheckConfig.setCheckAllColumn(check);
  }

  public static void setCheckExactIdentifier(boolean check) {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig sqlCheckConfig = sqlCheckConfigDeque.peek();
    sqlCheckConfig.setCheckExactIdentifier(check);
  }

  public static void setCheckDmlCondition(boolean check) {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig sqlCheckConfig = sqlCheckConfigDeque.peek();
    sqlCheckConfig.setCheckDmlCondition(check);
  }

  public static Boolean getCheckAllColumn() {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig sqlCheckConfig = sqlCheckConfigDeque.peek();
    return sqlCheckConfig.getCheckAllColumn();
  }

  public static Boolean getCheckExactIdentifier() {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig sqlCheckConfig = sqlCheckConfigDeque.peek();
    return sqlCheckConfig.getCheckExactIdentifier();
  }

  public static Boolean getCheckDmlCondition() {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig sqlCheckConfig = sqlCheckConfigDeque.peek();
    return sqlCheckConfig.getCheckDmlCondition();
  }

  public static void setDisable(Boolean aBoolean) {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig sqlCheckConfig = sqlCheckConfigDeque.peek();
    sqlCheckConfig.setDisable(aBoolean);
  }

  public static Boolean getDisable() {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig sqlCheckConfig = sqlCheckConfigDeque.peek();
    return sqlCheckConfig.getDisable();
  }

  public static void push(SqlCheckConfig sqlCheckConfig) {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    if (sqlCheckConfigDeque == null) {
      sqlCheckConfigDeque = new ArrayDeque<>();
      SQL_CHECK_TL.set(sqlCheckConfigDeque);
    }
    sqlCheckConfigDeque.push(sqlCheckConfig);
  }

  public static SqlCheckConfig pop() {
    Deque<SqlCheckConfig> sqlCheckConfigDeque = SQL_CHECK_TL.get();
    SqlCheckConfig pop = sqlCheckConfigDeque.pop();
    if (sqlCheckConfigDeque.isEmpty()) {
      SQL_CHECK_TL.remove();
    }
    return pop;
  }

  public static void remove() {
    SQL_CHECK_TL.remove();
  }

  public static boolean contextActive() {
    return SQL_CHECK_TL.get() != null;
  }

  public static SqlCheckConfig peek() {
    return SQL_CHECK_TL.get().peek();
  }

}
