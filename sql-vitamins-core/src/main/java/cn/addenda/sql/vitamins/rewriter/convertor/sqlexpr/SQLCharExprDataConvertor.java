package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;

/**
 * 不是java的char，是java的string
 *
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLCharExprDataConvertor extends AbstractSQLExprDataConvertor<SQLCharExpr> {

  @Override
  public String format(Object obj) {
    SQLCharExpr parse = parse(obj);
    return printChars(parse.getText());
  }

  /**
   * 此方法模仿druid的{@link com.alibaba.druid.sql.visitor.SQLASTOutputVisitor#printChars(java.lang.String)}
   */
  protected String printChars(String text) {
    StringBuilder sb = new StringBuilder();
    if (text == null) {
      sb.append("null");
    } else {
      sb.append('\'');
      int index = text.indexOf('\'');
      if (index >= 0) {
        text = text.replace("'", "''");
      }
      // druid里没有处理\字符，输出的字符串无法满足mysql的需要，我处理了一下。
      // 很奇怪的是，datagrip导出的\字符也没处理。实测datagrip导出的带\字符的insert语句无法执行。
      int index2 = text.indexOf('\\');
      if (index2 >= 0) {
        text = text.replace("\\", "\\\\");
      }
      sb.append(text);
      sb.append('\'');
    }
    return sb.toString();
  }

  @Override
  public SQLCharExpr doParse(String str) {
    return new SQLCharExpr(str);
  }

  @Override
  public boolean strMatch(String str) {
    return true;
  }

}
