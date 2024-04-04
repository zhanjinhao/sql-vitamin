package cn.addenda.sql.vitamins.rewriter.visitor.tablename;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableRenameVisitor extends MySqlASTVisitorAdapter {

  private static final Map<String, TableRenameVisitor> TABLE_RENAME_VISITOR_MAP = new ConcurrentHashMap<>();

  @Getter
  private final String sourceTableName;
  @Getter
  private final String targetTableName;

  private TableRenameVisitor(String sourceTableName, String targetTableName) {
    if (sourceTableName == null) {
      throw new SqlVitaminsException("`sourceTableName` can not be null!");
    }
    if (targetTableName == null) {
      throw new SqlVitaminsException("`targetTableName` can not be null!");
    }
    this.sourceTableName = removeGrave(sourceTableName);
    this.targetTableName = removeGrave(targetTableName);
  }

  public static TableRenameVisitor getInstance(String sourceTableName, String targetTableName) {
    return TABLE_RENAME_VISITOR_MAP.computeIfAbsent(sourceTableName + targetTableName,
        k -> new TableRenameVisitor(sourceTableName, targetTableName));
  }

  @Override
  public boolean visit(SQLExprTableSource x) {
    // 不递归子节点
    return false;
  }

  @Override
  public void endVisit(SQLExprTableSource x) {
    SQLExpr expr = x.getExpr();
    // t_dynamic_name_test_20240306
    if (expr instanceof SQLIdentifierExpr) {
      SQLIdentifierExpr sQLIdentifierExpr = (SQLIdentifierExpr) expr;
      String tableNameFromSql = sQLIdentifierExpr.getName();
      // `foc_planning`.`t_dynamic_name_test_20240306`.`content` 格式正确
      // `foc_planning.t_dynamic_name_test_20240306.content` 格式错误
      if (sourceTableName.equals(removeGrave(tableNameFromSql))) {
        sQLIdentifierExpr.setName(targetTableName(tableNameFromSql, targetTableName));
      }
    }
    // foc_planning.t_dynamic_name_test_20240306
    else if (expr instanceof SQLPropertyExpr) {
      SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) expr;
      String tableNameFromSql = sqlPropertyExpr.getSimpleName();
      if (sourceTableName.equals(removeGrave(tableNameFromSql))) {
        sqlPropertyExpr.setName(targetTableName(tableNameFromSql, targetTableName));
      }
    }
  }

  /**
   * SQLExprTableSource下的SQLPropertyExpr节点（表）不会走到这一步，能走到这一步的是列名。
   * <ul>
   *     <li>
   *         planning.t_dynamic_name_test_20240306.content：中间这段是表名
   *     </li>
   *     <li>
   *         t_dynamic_name_test_20240306.content：第一段是表名
   *     </li>
   *     <li>
   *         content ：SQLIdentifierExpr
   *     </li>
   * </ul>
   */
  @Override
  public boolean visit(SQLPropertyExpr x) {
    SQLExpr owner = x.getOwner();
    if (owner instanceof SQLPropertyExpr) {
      SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) owner;
      String tableNameFromSql = sqlPropertyExpr.getSimpleName();
      if (sourceTableName.equals(removeGrave(tableNameFromSql))) {
        sqlPropertyExpr.setName(targetTableName(tableNameFromSql, targetTableName));
      }
    } else {
      String tableNameFromSql = x.getOwnerName();
      if (sourceTableName.equals(removeGrave(tableNameFromSql))) {
        x.setOwner(targetTableName(tableNameFromSql, targetTableName));
      }
    }
    return false;
  }

  @Override
  public void endVisit(SQLPropertyExpr x) {
    // nop
  }

  private String targetTableName(String nameFromSql, String targetTableName) {
    boolean graveFlag = nameFromSql.contains("`");
    if (!graveFlag) {
      return targetTableName;
    }
    return "`" + targetTableName + "`";
  }

  private static String removeGrave(String str) {
    if (str != null && str.length() >= 2 && str.startsWith("`") && str.endsWith("`")) {
      // 前后都有反引号，去掉首尾的反引号
      return str.substring(1, str.length() - 1);
    }
    // 没有满足条件或者字符串长度不足
    return str;
  }

  @Override
  public String toString() {
    return "TableRenameVisitor{" +
        "sourceTableName='" + sourceTableName + '\'' +
        ", targetTableName='" + targetTableName + '\'' +
        "} " + super.toString();
  }
}

