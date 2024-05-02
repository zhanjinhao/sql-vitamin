package cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLNullExpr;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLNullExprDataConvertor extends AbstractSQLExprDataConvertor<SQLNullExpr> {

  @Override
  public String format(Object obj) {
    return "null";
  }

  @Override
  public SQLNullExpr doParse(String str) {
    return new SQLNullExpr();
  }

  @Override
  public boolean strMatch(String str) {
    return "null".equalsIgnoreCase(str);
  }

}
