package cn.addenda.sql.vitamin.rewriter.visitor.identifier;

import cn.addenda.sql.vitamin.rewriter.visitor.SqlBoundVisitor;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/5/6 20:33
 */
public class StarOfSelectItemExistsVisitor extends SqlBoundVisitor<SQLSelectStatement> {

  private final boolean visitAggregateFunction;

  @Getter
  private boolean exists = false;

  public StarOfSelectItemExistsVisitor(String sql, boolean visitAggregateFunction) {
    super(sql);
    this.visitAggregateFunction = visitAggregateFunction;
  }

  public StarOfSelectItemExistsVisitor(String sql) {
    this(sql, true);
  }

  public StarOfSelectItemExistsVisitor(SQLSelectStatement sql, boolean visitAggregateFunction) {
    super(sql);
    this.visitAggregateFunction = visitAggregateFunction;
  }

  public StarOfSelectItemExistsVisitor(SQLSelectStatement sql) {
    this(sql, true);
  }

  @Override
  public void endVisit(SQLPropertyExpr x) {
    if ("*".equals(x.getName())) {
      exists = true;
    }
  }

  @Override
  public void endVisit(SQLAllColumnExpr x) {
    exists = true;
  }

  @Override
  public boolean visit(SQLAggregateExpr x) {
    return visitAggregateFunction;
  }

  @Override
  public SQLSelectStatement visit() {
    sqlStatement.accept(this);
    return sqlStatement;
  }

  @Override
  public String toString() {
    return "SelectItemStarExistsVisitor{" +
      "visitAggregateFunction=" + visitAggregateFunction +
      "} " + super.toString();
  }
}
