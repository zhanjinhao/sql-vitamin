package cn.addenda.sql.vitamins.rewriter.util;

import cn.addenda.sql.vitamins.rewriter.visitor.identifier.ParameterCountVisitor;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

import static com.alibaba.druid.sql.visitor.VisitorFeature.OutputUCase;

/**
 * @author addenda
 * @since 2023/4/28 21:11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DruidSQLUtils extends SQLUtils {

  public static String toLowerCaseSQL(SQLObject sqlObject) {
    return toLowerCaseSQL(sqlObject, false);
  }

  public static String toLowerCaseSQL(SQLObject sqlObject, boolean printEnter) {
    if (sqlObject == null) {
      return null;
    }
    StringBuilder out = new StringBuilder();
    MySqlOutputVisitor visitor = new MySqlOutputVisitor(out, false);

    visitor.setUppCase(false);
    // 兼容druid某些版本setUppCase不生效的问题
    visitor.config(OutputUCase, false);

    visitor.setParameterized(false);
    visitor.setPrettyFormat(printEnter);
    sqlObject.accept(visitor);
    return out.toString();
  }

  public static String removeEnter(String sql) {
    StringBuilder stringBuilder = new StringBuilder();
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
    for (SQLStatement statement : sqlStatements) {
      stringBuilder.append(DruidSQLUtils.toLowerCaseSQL(statement)).append("\n");
    }
    return stringBuilder.toString().trim();
  }

  public static String statementMerge(String sql, Function<SQLStatement, String> function) {
    List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
    StringBuilder stringBuilder = new StringBuilder();
    for (SQLStatement sqlStatement : stmtList) {
      stringBuilder.append(function.apply(sqlStatement)).append("\n");
    }
    return stringBuilder.toString().trim();
  }

  public static int calculateParameterCount(String parameterizedSql) {
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(parameterizedSql, DbType.mysql);
    int count = 0;
    for (SQLStatement sqlStatement : sqlStatements) {
      ParameterCountVisitor parameterCountVisitor = new ParameterCountVisitor(sqlStatement);
      parameterCountVisitor.visit();
      count += parameterCountVisitor.getParameterCount();
    }
    return count;
  }

  public static String extractDmlTableName(String sql) {
    SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, DbType.mysql);
    if (sqlStatement instanceof SQLUpdateStatement) {
      SQLUpdateStatement sqlUpdateStatement = (SQLUpdateStatement) sqlStatement;
      return sqlUpdateStatement.getTableName().getSimpleName();
    } else if (sqlStatement instanceof SQLDeleteStatement) {
      SQLDeleteStatement sqlDeleteStatement = (SQLDeleteStatement) sqlStatement;
      return sqlDeleteStatement.getTableName().getSimpleName();
    } else if (sqlStatement instanceof SQLInsertStatement) {
      SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) sqlStatement;
      return sqlInsertStatement.getTableName().getSimpleName();
    }

    return null;
  }

  public static String extractDmlWhereCondition(SQLStatement sqlStatement) {
    if (sqlStatement instanceof SQLUpdateStatement) {
      SQLUpdateStatement sqlUpdateStatement = (SQLUpdateStatement) sqlStatement;
      return sqlUpdateStatement.getWhere().toString();
    } else if (sqlStatement instanceof SQLDeleteStatement) {
      SQLDeleteStatement sqlDeleteStatement = (SQLDeleteStatement) sqlStatement;
      return sqlDeleteStatement.getWhere().toString();
    }

    return null;
  }

  public static <T extends SQLStatement> T replaceDmlWhereCondition(T sqlStatement, String where) {
    if (sqlStatement instanceof SQLUpdateStatement) {
      SQLUpdateStatement updateStatement = (SQLUpdateStatement) sqlStatement;
      updateStatement.setWhere(SQLUtils.toSQLExpr(where));
      return (T) updateStatement;
    } else if (sqlStatement instanceof SQLDeleteStatement) {
      SQLDeleteStatement sqlDeleteStatement = (SQLDeleteStatement) sqlStatement;
      sqlDeleteStatement.setWhere(SQLUtils.toSQLExpr(where));
      return (T) sqlDeleteStatement;
    }

    return null;
  }

}