package cn.addenda.sql.vitamins.rewriter.visitor.identifier;

import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;

import java.util.List;

/**
 * insert有insert select语法，所以将InsertOrUpdateItem的检测和SelectItem分开。
 *
 * @author addenda
 * @since 2023/5/13 17:26
 */
public class IdentifierOfInsertUpdateItemExistsVisitor extends IdentifierExistsVisitor {

  public IdentifierOfInsertUpdateItemExistsVisitor(
    String sql, String identifier,
    List<String> included, List<String> excluded) {
    super(sql, identifier, included, excluded);
  }

  public IdentifierOfInsertUpdateItemExistsVisitor(
    SQLStatement sql, String identifier,
    List<String> included, List<String> excluded) {
    super(sql, identifier, included, excluded);
  }

  public IdentifierOfInsertUpdateItemExistsVisitor(String sql, String identifier) {
    super(sql, identifier);
  }

  public IdentifierOfInsertUpdateItemExistsVisitor(SQLStatement sql, String identifier) {
    super(sql, identifier);
  }

  @Override
  public void endVisit(SQLPropertyExpr x) {
  }

  @Override
  public void endVisit(SQLIdentifierExpr x) {
  }

  @Override
  public void endVisit(MySqlInsertStatement x) {
    List<SQLExpr> columns = x.getColumns();
    List<String> identifierList = identifierListStack.peek();

    for (SQLExpr column : columns) {
      String name = JdbcSQLUtils.extractColumnName(DruidSQLUtils.toLowerCaseSQL(column));
      if (identifier == null || name.equalsIgnoreCase(identifier)) {
        identifierList.add(name);
      }
    }
    super.endVisit(x);
  }

  @Override
  public void endVisit(MySqlUpdateStatement x) {
    List<SQLUpdateSetItem> items = x.getItems();
    List<String> identifierList = identifierListStack.peek();
    for (SQLUpdateSetItem sqlUpdateSetItem : items) {
      SQLExpr column = sqlUpdateSetItem.getColumn();
      String name = JdbcSQLUtils.extractColumnName(DruidSQLUtils.toLowerCaseSQL(column));
      if (identifier == null || name.equalsIgnoreCase(identifier)) {
        identifierList.add(name);
      }
    }
    super.endVisit(x);
  }

}
