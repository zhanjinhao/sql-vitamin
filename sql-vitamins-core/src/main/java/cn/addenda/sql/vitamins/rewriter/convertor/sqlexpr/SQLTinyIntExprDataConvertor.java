package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLTinyIntExpr;

import java.math.BigInteger;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLTinyIntExprDataConvertor extends AbstractSQLExprDataConvertor<SQLTinyIntExpr> {

  @Override
  public String format(Object obj) {
    SQLTinyIntExpr parse = parse(obj);
    return parse.getNumber().toString();
  }

  @Override
  public SQLTinyIntExpr doParse(String str) {
    return new SQLTinyIntExpr(Byte.parseByte(str));
  }

  private static final BigInteger min = BigInteger.valueOf(Byte.MIN_VALUE);
  private static final BigInteger max = BigInteger.valueOf(Byte.MAX_VALUE);

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
