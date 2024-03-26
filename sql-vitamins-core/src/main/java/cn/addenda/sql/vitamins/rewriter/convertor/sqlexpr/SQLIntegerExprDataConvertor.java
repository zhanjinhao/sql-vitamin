package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;

import java.math.BigInteger;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLIntegerExprDataConvertor extends AbstractSQLExprDataConvertor<SQLIntegerExpr> {

  @Override
  public String format(Object obj) {
    SQLIntegerExpr parse = parse(obj);
    return parse.getValue().toString();
  }

  @Override
  public SQLIntegerExpr doParse(String str) {
    return new SQLIntegerExpr(new BigInteger(str));
  }

  @Override
  public boolean strMatch(String str) {
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (!Character.isDigit(c)) {
        return false;
      }
    }
    return true;
  }

}
