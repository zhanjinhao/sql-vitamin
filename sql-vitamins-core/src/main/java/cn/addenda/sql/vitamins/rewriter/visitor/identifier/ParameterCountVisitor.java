package cn.addenda.sql.vitamins.rewriter.visitor.identifier;

import cn.addenda.sql.vitamins.rewriter.visitor.SqlBoundVisitor;
import cn.addenda.sql.vitamins.rewriter.visitor.ViewToTableVisitor;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/6/15 20:59
 */
public class ParameterCountVisitor extends SqlBoundVisitor<SQLStatement> {

  @Getter
  private int parameterCount = 0;

  public ParameterCountVisitor(String sql) {
    super(sql);
  }

  public ParameterCountVisitor(SQLStatement sqlStatement) {
    super(sqlStatement);
  }

  @Override
  public void endVisit(SQLVariantRefExpr x) {
    parameterCount++;
  }

  @Override
  public SQLStatement visit() {
    sqlStatement.accept(ViewToTableVisitor.getInstance());
    sqlStatement.accept(this);
    return sqlStatement;
  }
}
