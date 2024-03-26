package cn.addenda.sql.vitamins.rewriter.visitor.condition;

import cn.addenda.sql.vitamins.rewriter.visitor.SQLBoundVisitor;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/5/22 21:39
 */
public class DmlConditionExistsVisitor extends SQLBoundVisitor<SQLStatement> {

  @Getter
  private boolean exists = false;

  public DmlConditionExistsVisitor(String sql) {
    super(sql);
  }

  public DmlConditionExistsVisitor(SQLStatement sqlStatement) {
    super(sqlStatement);
  }

  @Override
  public void endVisit(MySqlUpdateStatement x) {
    exists = x.getWhere() != null;
  }

  @Override
  public void endVisit(MySqlDeleteStatement x) {
    exists = x.getWhere() != null;
  }

  @Override
  public SQLStatement visit() {
    sqlStatement.accept(this);
    return sqlStatement;
  }

}
