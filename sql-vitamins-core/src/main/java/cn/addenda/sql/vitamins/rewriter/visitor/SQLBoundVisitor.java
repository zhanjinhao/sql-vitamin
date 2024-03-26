package cn.addenda.sql.vitamins.rewriter.visitor;

import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author addenda
 * @since 2023/5/9 18:10
 */
public abstract class SQLBoundVisitor<T extends SQLStatement> extends MySqlASTVisitorAdapter {

  @Getter
  @Setter
  private boolean toLowerCase = true;

  @Getter
  protected String sql;

  @Getter
  protected T sqlStatement;

  protected SQLBoundVisitor(String sql) {
    this.sql = sql;
    this.init();
  }

  protected SQLBoundVisitor(T sqlStatement) {
    this.sqlStatement = sqlStatement;
    this.init();
  }

  private void init() {
    if (sqlStatement == null && sql == null) {
      throw new NullPointerException("SQL 不存在！");
    }
    if (sql == null) {
      this.sql = DruidSQLUtils.toLowerCaseSQL(sqlStatement, toLowerCase);
    }
    if (sqlStatement == null) {
      List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
      if (stmtList.size() != 1) {
        String msg = String.format("仅支持单条SQL，当前有[%s]条SQL。内容：[%s]。", stmtList.size(), sql);
        throw new IllegalArgumentException(msg);
      }
      this.sqlStatement = (T) stmtList.get(0);
    }
  }

  public abstract T visit();

  @Override
  public String toString() {
    return "SQLBoundVisitor{" +
        "toLowerCase=" + toLowerCase +
        ", sql='" + sql + '\'' +
        "} " + super.toString();
  }
}
