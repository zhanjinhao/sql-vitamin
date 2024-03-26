package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLDoubleExpr;

import java.math.BigDecimal;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLDoubleExprDataConvertor extends AbstractSQLExprDataConvertor<SQLDoubleExpr> {

  @Override
  public String format(Object obj) {
    SQLDoubleExpr parse = parse(obj);
    return parse.getNumber().toString();
  }

  @Override
  public SQLDoubleExpr doParse(String str) {
    return new SQLDoubleExpr(new BigDecimal(str).doubleValue());
  }

  private static final BigDecimal max = BigDecimal.valueOf(Double.MAX_VALUE);
  private static final BigDecimal min = BigDecimal.valueOf(Double.MIN_VALUE);

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
