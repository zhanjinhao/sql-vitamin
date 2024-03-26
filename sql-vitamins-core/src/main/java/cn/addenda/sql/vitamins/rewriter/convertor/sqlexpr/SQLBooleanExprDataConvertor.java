package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLBooleanExprDataConvertor extends AbstractSQLExprDataConvertor<SQLBooleanExpr> {

  @Override
  public String format(Object obj) {
    SQLBooleanExpr parse = parse(obj);
    return parse.getValue().toString();
  }

  @Override
  public SQLBooleanExpr doParse(String str) {
    return new SQLBooleanExpr(Boolean.parseBoolean(str));
  }

  @Override
  public boolean strMatch(String str) {
    return "true".equalsIgnoreCase(str.trim()) || "false".equalsIgnoreCase(str.trim());
  }

}
