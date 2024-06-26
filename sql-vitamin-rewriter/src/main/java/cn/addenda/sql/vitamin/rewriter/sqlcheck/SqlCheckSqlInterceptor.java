package cn.addenda.sql.vitamin.rewriter.sqlcheck;

import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityContext;
import cn.addenda.sql.vitamin.rewriter.AbstractSqlInterceptor;
import cn.addenda.sql.vitamin.rewriter.util.JdbcSQLUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

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
@Slf4j
public class SqlCheckSqlInterceptor extends AbstractSqlInterceptor {

  private final SqlChecker sqlChecker;
  private final boolean defaultDisable;
  private final boolean defaultCheckAllColumn;
  private final boolean defaultCheckExactIdentifier;
  private final boolean defaultCheckDmlCondition;

  public SqlCheckSqlInterceptor(
      boolean removeEnter, SqlChecker sqlChecker, boolean disable,
      boolean checkAllColumn, boolean checkExactIdentifier, boolean checkDmlCondition) {
    super(removeEnter);
    if (sqlChecker == null) {
      throw new SqlCheckException("`sqlChecker` can not be null!");
    }
    this.sqlChecker = sqlChecker;
    this.defaultDisable = disable;
    this.defaultCheckAllColumn = checkAllColumn;
    this.defaultCheckExactIdentifier = checkExactIdentifier;
    this.defaultCheckDmlCondition = checkDmlCondition;
  }

  @Override
  public String rewrite(String sql) {

    logDebug("Sql Check - start: {}.", sql);
    if (!SqlCheckContext.contextActive()) {
      if (!defaultDisable) {
        try {
          SqlCheckContext.push(new SqlCheckConfig());
          sql = doRewrite(sql);
        } finally {
          SqlCheckContext.pop();
        }
      }
    } else {
      Boolean disable = JdbcSQLUtils.getOrDefault(BaseEntityContext.getDisable(), defaultDisable);
      if (Boolean.FALSE.equals(disable)) {
        sql = doRewrite(sql);
      }
    }
    logDebug("Sql Check - end: {}.", sql);

    return sql;
  }

  private String doRewrite(String sql) {
    Boolean checkAllColumn =
        JdbcSQLUtils.getOrDefault(SqlCheckContext.getCheckAllColumn(), defaultCheckAllColumn);
    if (Boolean.TRUE.equals(checkAllColumn)
        && JdbcSQLUtils.isSelect(sql)
        && (sqlChecker.allColumnExists(sql))) {
      String msg = String.format("SQL: [%s], 返回字段包含了*或tableName.*语法！", removeEnter(sql));
      throw new SqlCheckException(msg);
    }
    Boolean checkExactIdentifier =
        JdbcSQLUtils.getOrDefault(SqlCheckContext.getCheckExactIdentifier(), defaultCheckExactIdentifier);
    if (Boolean.TRUE.equals(checkExactIdentifier)
        && JdbcSQLUtils.isSelect(sql)
        && !sqlChecker.exactIdentifier((sql))) {
      String msg = String.format("SQL: [%s], 存在不精确的字段！", removeEnter(sql));
      throw new SqlCheckException(msg);
    }
    Boolean heckDmlCondition =
        JdbcSQLUtils.getOrDefault(SqlCheckContext.getCheckDmlCondition(), defaultCheckDmlCondition);
    if (Boolean.TRUE.equals(heckDmlCondition)
        && (JdbcSQLUtils.isUpdate(sql) || JdbcSQLUtils.isDelete(sql))
        && !sqlChecker.dmlConditionExists((sql))) {
      String msg = String.format("SQL: [%s], 没有条件！", removeEnter(sql));
      throw new SqlCheckException(msg);
    }
    return sql;
  }

  @Override
  public int order() {
    return MAX / 2 - 90000;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
