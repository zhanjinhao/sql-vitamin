package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;

import java.math.BigDecimal;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLNumberExprDataConvertor extends AbstractSQLExprDataConvertor<SQLNumberExpr> {

  @Override
  public String format(Object obj) {
    SQLNumberExpr parse = parse(obj);
    return parse.getNumber().toString();
  }

  @Override
  public SQLNumberExpr doParse(String str) {
    return new SQLNumberExpr(new BigDecimal(str));
  }

  @Override
  public boolean strMatch(String str) {
    int dotCount = 0;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == '.') {
        dotCount++;
        continue;
      }
      if (!Character.isDigit(c)) {
        return false;
      }
    }
    return dotCount <= 1;
  }

}
