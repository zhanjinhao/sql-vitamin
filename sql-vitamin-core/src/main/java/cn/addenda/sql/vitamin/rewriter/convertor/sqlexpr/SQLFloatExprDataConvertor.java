package cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLFloatExpr;

import java.math.BigDecimal;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLFloatExprDataConvertor extends AbstractSQLExprDataConvertor<SQLFloatExpr> {

  @Override
  public String format(Object obj) {
    SQLFloatExpr parse = parse(obj);
    return parse.getNumber().toString();
  }

  @Override
  public SQLFloatExpr doParse(String str) {
    return new SQLFloatExpr(new BigDecimal(str).floatValue());
  }

  private static final BigDecimal max = BigDecimal.valueOf(Float.MAX_VALUE);
  private static final BigDecimal min = BigDecimal.valueOf(Float.MIN_VALUE);

  @Override
  public boolean strMatch(String str) {
    int dotCount = 0;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == '.') {
        dotCount++;
        if (dotCount > 1) {
          return false;
        }
        continue;
      }
      if (!Character.isDigit(c)) {
        return false;
      }
    }
    BigDecimal bigDecimal = new BigDecimal(str);
    if (bigDecimal.compareTo(min) >= 0 && bigDecimal.compareTo(max) <= 0) {
      return true;
    }
    return false;
  }

}
