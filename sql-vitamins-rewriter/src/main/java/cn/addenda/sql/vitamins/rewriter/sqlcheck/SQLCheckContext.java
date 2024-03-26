package cn.addenda.sql.vitamins.rewriter.sqlcheck;

import lombok.*;

import java.util.Stack;

/**
 * @author addenda
 * @since 2023/5/7 15:55
 */
public class SQLCheckContext {

  private SQLCheckContext() {
  }

  private static final ThreadLocal<Stack<SQLCheckConfig>> SQL_CHECK_TL = ThreadLocal.withInitial(() -> null);

  public static void setCheckAllColumn(boolean check) {
    Stack<SQLCheckConfig> sqlCheckConfigs = SQL_CHECK_TL.get();
    SQLCheckConfig sqlCheckConfig = sqlCheckConfigs.peek();
    sqlCheckConfig.setCheckAllColumn(check);
  }

  public static void setCheckExactIdentifier(boolean check) {
    Stack<SQLCheckConfig> sqlCheckConfigs = SQL_CHECK_TL.get();
    SQLCheckConfig sqlCheckConfig = sqlCheckConfigs.peek();
    sqlCheckConfig.setCheckExactIdentifier(check);
  }

  public static void setCheckDmlCondition(boolean check) {
    Stack<SQLCheckConfig> sqlCheckConfigs = SQL_CHECK_TL.get();
    SQLCheckConfig sqlCheckConfig = sqlCheckConfigs.peek();
    sqlCheckConfig.setCheckDmlCondition(check);
  }

  public static Boolean getCheckAllColumn() {
    Stack<SQLCheckConfig> sqlCheckConfigs = SQL_CHECK_TL.get();
    SQLCheckConfig sqlCheckConfig = sqlCheckConfigs.peek();
    return sqlCheckConfig.getCheckAllColumn();
  }

  public static Boolean getCheckExactIdentifier() {
    Stack<SQLCheckConfig> sqlCheckConfigs = SQL_CHECK_TL.get();
    SQLCheckConfig sqlCheckConfig = sqlCheckConfigs.peek();
    return sqlCheckConfig.getCheckExactIdentifier();
  }

  public static Boolean getCheckDmlCondition() {
    Stack<SQLCheckConfig> sqlCheckConfigs = SQL_CHECK_TL.get();
    SQLCheckConfig sqlCheckConfig = sqlCheckConfigs.peek();
    return sqlCheckConfig.getCheckDmlCondition();
  }

  public static void push() {
    push(new SQLCheckConfig());
  }

  public static void push(SQLCheckConfig sqlCheckConfig) {
    Stack<SQLCheckConfig> sqlCheckConfigs = SQL_CHECK_TL.get();
    if (sqlCheckConfigs == null) {
      sqlCheckConfigs = new Stack<>();
      SQL_CHECK_TL.set(sqlCheckConfigs);
    }
    sqlCheckConfigs.push(sqlCheckConfig);
  }

  public static void pop() {
    Stack<SQLCheckConfig> sqlCheckConfigs = SQL_CHECK_TL.get();
    sqlCheckConfigs.pop();
    if (sqlCheckConfigs.isEmpty()) {
      SQL_CHECK_TL.remove();
    }
  }

  public static boolean contextActive() {
    return SQL_CHECK_TL.get() != null;
  }

  public static SQLCheckConfig peek() {
    return SQL_CHECK_TL.get().peek();
  }

  @Setter
  @Getter
  @ToString
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SQLCheckConfig {
    private Boolean checkAllColumn;
    private Boolean checkExactIdentifier;
    private Boolean checkDmlCondition;

    public SQLCheckConfig(SQLCheckConfig sqlCheckConfig) {
      this.checkAllColumn = sqlCheckConfig.checkAllColumn;
      this.checkExactIdentifier = sqlCheckConfig.checkExactIdentifier;
      this.checkDmlCondition = sqlCheckConfig.checkDmlCondition;
    }
  }

}
