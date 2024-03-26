package cn.addenda.sql.vitamins.rewriter.sqlcheck;

import cn.addenda.sql.vitamins.rewriter.ConnectionPrepareStatementInterceptor;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;

/**
 * 只要配置了拦截器就会执行SQL拦截
 * <p>
 * - 返回值为*或tableName.*检测 <br/>
 * - 精确标识符检测 <br/>
 * - DML语句需要有条件 <br/>
 *
 * @author addenda
 * @since 2023/5/7 15:53
 */
public class SQLCheckInterceptor extends ConnectionPrepareStatementInterceptor {

  private final SQLChecker sqlChecker;
  private final boolean checkAllColumn;
  private final boolean checkExactIdentifier;
  private final boolean checkDmlCondition;

  public SQLCheckInterceptor() {
    this.sqlChecker = new DruidSQLChecker();
    this.checkAllColumn = true;
    this.checkExactIdentifier = true;
    this.checkDmlCondition = true;
  }

  public SQLCheckInterceptor(SQLChecker sqlChecker) {
    this.sqlChecker = sqlChecker;
    this.checkAllColumn = true;
    this.checkExactIdentifier = true;
    this.checkDmlCondition = true;
  }

  public SQLCheckInterceptor(boolean removeEnter, SQLChecker sqlChecker, boolean checkAllColumn,
                             boolean checkExactIdentifier, boolean checkDmlCondition) {
    super(removeEnter);
    this.sqlChecker = sqlChecker;
    this.checkAllColumn = checkAllColumn;
    this.checkExactIdentifier = checkExactIdentifier;
    this.checkDmlCondition = checkDmlCondition;
  }

  @Override
  protected String process(String sql) {
    if (!SQLCheckContext.contextActive()) {
      return sql;
    }

    if (checkAllColumn && Boolean.TRUE.equals(SQLCheckContext.getCheckAllColumn())
      && JdbcSQLUtils.isSelect(sql) && sqlChecker.allColumnExists(sql)) {
      String msg = String.format("SQL: [%s], 返回字段包含了*或tableName.*语法！", removeEnter(sql));
      throw new SQLCheckException(msg);
    }
    if (checkExactIdentifier && Boolean.TRUE.equals(SQLCheckContext.getCheckExactIdentifier())
      && JdbcSQLUtils.isSelect(sql) && !sqlChecker.exactIdentifier((sql))) {
      String msg = String.format("SQL: [%s], 存在不精确的字段！", removeEnter(sql));
      throw new SQLCheckException(msg);
    }
    if (checkDmlCondition && Boolean.TRUE.equals(SQLCheckContext.getCheckDmlCondition())
      && (JdbcSQLUtils.isUpdate(sql) || JdbcSQLUtils.isDelete(sql)) && !sqlChecker.dmlConditionExists((sql))) {
      String msg = String.format("SQL: [%s], 没有条件！", removeEnter(sql));
      throw new SQLCheckException(msg);
    }

    return sql;
  }

  @Override
  public int order() {
    return MAX / 2 - 90000;
  }

}
