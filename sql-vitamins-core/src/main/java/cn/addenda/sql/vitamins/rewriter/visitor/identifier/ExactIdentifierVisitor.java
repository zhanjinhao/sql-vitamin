package cn.addenda.sql.vitamins.rewriter.visitor.identifier;

import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.ViewToTableVisitor;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * @author addenda
 * @since 2023/5/7 11:42
 */
public class ExactIdentifierVisitor extends AbstractIdentifierVisitor {

  @Getter
  private boolean exact;

  public ExactIdentifierVisitor(String sql) {
    super(sql, null);
    this.exact = true;
  }

  public ExactIdentifierVisitor(SQLStatement sql) {
    super(sql, null);
    this.exact = true;
  }

  @Override
  public SQLStatement visit() {
    sqlStatement.accept(ViewToTableVisitor.getInstance());
    sqlStatement.accept(this);
    return sqlStatement;
  }

  @Override
  public void endVisit(SQLSelectQueryBlock x) {
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getFrom());
    List<String> identifierList = identifierListStack.peek();
    handle(identifierList, viewToTableMap, x);
    super.endVisit(x);
  }

  private void handle(List<String> identifierList, Map<String, String> viewToTableMap, SQLObject x) {
    if (viewToTableMap.size() == 1) {
      // no-op
    } else {
      for (String identifier : identifierList) {
        String owner = JdbcSQLUtils.extractColumnOwner(identifier);
        if (owner == null) {
          exact = false;
          break;
        }
      }
    }
  }

  @Override
  public void endVisit(MySqlDeleteStatement x) {
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getTableSource());
    List<String> identifierList = identifierListStack.peek();
    handle(identifierList, viewToTableMap, x);

    super.endVisit(x);
  }

  @Override
  public void endVisit(MySqlUpdateStatement x) {
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getTableSource());
    List<String> identifierList = identifierListStack.peek();
    handle(identifierList, viewToTableMap, x);

    super.endVisit(x);
  }

  @Override
  public void endVisit(MySqlInsertStatement x) {
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getTableSource());
    List<String> identifierList = identifierListStack.peek();
    handle(identifierList, viewToTableMap, x);

    super.endVisit(x);
  }

  /**
   * 不精准的语法：<br/>
   * - using<br/>
   * - nature join<br/>
   */
  @Override
  public void endVisit(SQLJoinTableSource x) {
    SQLJoinTableSource.JoinType joinType = x.getJoinType();
    if (SQLJoinTableSource.JoinType.NATURAL_JOIN == joinType ||
      SQLJoinTableSource.JoinType.NATURAL_CROSS_JOIN == joinType ||
      SQLJoinTableSource.JoinType.NATURAL_INNER_JOIN == joinType ||
      SQLJoinTableSource.JoinType.NATURAL_LEFT_JOIN == joinType ||
      SQLJoinTableSource.JoinType.NATURAL_RIGHT_JOIN == joinType
    ) {
      exact = false;
    }
    List<SQLExpr> using = x.getUsing();
    if (using != null && !using.isEmpty()) {
      exact = false;
    }
  }

}
