package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLBigIntExpr;

import java.math.BigInteger;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLBigIntExprDataConvertor extends AbstractSQLExprDataConvertor<SQLBigIntExpr> {

  @Override
  public String format(Object obj) {
    SQLBigIntExpr parse = parse(obj);
    return parse.getNumber().toString();
  }

  @Override
  public SQLBigIntExpr doParse(String str) {
    return new SQLBigIntExpr(new BigInteger(str).longValue());
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
