package cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLSmallIntExpr;

import java.math.BigInteger;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLSmallIntExprDataConvertor extends AbstractSQLExprDataConvertor<SQLSmallIntExpr> {

  @Override
  public String format(Object obj) {
    SQLSmallIntExpr parse = parse(obj);
    return parse.getNumber().toString();
  }

  @Override
  public SQLSmallIntExpr doParse(String str) {
    return new SQLSmallIntExpr(Short.parseShort(str));
  }

  private static final BigInteger min = BigInteger.valueOf(Short.MIN_VALUE);
  private static final BigInteger max = BigInteger.valueOf(Short.MAX_VALUE);

  @Override
  public boolean strMatch(String str) {
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (!Character.isDigit(c)) {
        return false;
      }
    }
    BigInteger bigInteger = new BigInteger(str);
    if (bigInteger.compareTo(min) >= 0 && bigInteger.compareTo(max) <= 0) {
      return true;
    }
    return false;
  }

}
