package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLCharExprDataConvertor extends AbstractSQLExprDataConvertor<SQLCharExpr> {

  @Override
  public String format(Object obj) {
    SQLCharExpr parse = parse(obj);
    return printChars(parse.getText());
  }

  protected String printChars(String text) {
    StringBuilder sb = new StringBuilder();
    if (text == null) {
      sb.append("null");
    } else {
      sb.append('\'');
      int index = text.indexOf('\'');
      if (index >= 0) {
        text = text.replaceAll("'", "''");
      }
      int index2 = text.indexOf('\\');
      if (index2 >= 0) {
        text = text.replaceAll("\\\\", "\\\\\\\\");
      }
      sb.append(text);
      sb.append('\'');
    }
    return sb.toString();
  }

  @Override
  public SQLCharExpr doParse(String str) {
    return new SQLCharExpr(String.valueOf(str));
  }

  @Override
  public boolean strMatch(String str) {
    return str.length() == 1;
  }

}
