package cn.addenda.sql.vitamin.rewriter.visitor.identifier;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * @author addenda
 * @since 2023/5/5 20:50
 */
public class IdentifierOfSelectItemExistsVisitor extends IdentifierExistsVisitor {

  @Getter
  private final Deque<Boolean> flagStack = new ArrayDeque<>();

  public IdentifierOfSelectItemExistsVisitor(
    String sql, String identifier, List<String> included, List<String> excluded) {
    super(sql, identifier, included, excluded);
  }

  public IdentifierOfSelectItemExistsVisitor(
    SQLStatement sql, String identifier, List<String> included, List<String> excluded) {
    super(sql, identifier, included, excluded);
  }

  public IdentifierOfSelectItemExistsVisitor(String sql, String identifier) {
    super(sql, identifier);
  }

  public IdentifierOfSelectItemExistsVisitor(SQLStatement sql, String identifier) {
    super(sql, identifier);
  }

  @Override
  public void endVisit(SQLSelectItem x) {
    super.endVisit(x);
    flagStack.pop();
  }

  @Override
  public boolean visit(SQLSelectItem x) {
    flagStack.push(true);
    return super.visit(x);
  }

  @Override
  public void endVisit(SQLPropertyExpr x) {
    if (Boolean.FALSE.equals(flagStack.peek())) {
      return;
    }
    super.endVisit(x);
  }

  @Override
  public void endVisit(SQLIdentifierExpr x) {
    if (Boolean.FALSE.equals(flagStack.peek())) {
      return;
    }
    super.endVisit(x);
  }

  @Override
  public void endVisit(SQLSelectQueryBlock x) {
    flagStack.pop();
    super.endVisit(x);
  }

  @Override
  public boolean visit(SQLSelectQueryBlock x) {
    // 进入Select块里就标记为false，哪些地方需要被扫描就标记为true
    flagStack.push(false);
    super.visit(x);
    return true;
  }

}
