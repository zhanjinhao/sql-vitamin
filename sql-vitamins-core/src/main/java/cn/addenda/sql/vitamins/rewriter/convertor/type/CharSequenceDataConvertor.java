package cn.addenda.sql.vitamins.rewriter.convertor.type;

import cn.addenda.sql.vitamins.rewriter.convertor.AbstractDataConvertor;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr.SQLCharExprDataConvertor;
import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;

import java.util.List;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class CharSequenceDataConvertor extends AbstractDataConvertor<CharSequence, SQLExpr> {

  private final DataConvertorRegistry dataConvertorRegistry;

  private final SQLCharExprDataConvertor sqlCharExprDataConvertor = new SQLCharExprDataConvertor();

  public CharSequenceDataConvertor(DataConvertorRegistry dataConvertorRegistry) {
    this.dataConvertorRegistry = dataConvertorRegistry;
  }

  @Override
  public String format(Object obj) {
    SQLExpr parse = parse(obj);
    return sqlCharExprDataConvertor.format(parse);
  }

  @Override
  public SQLExpr doParse(Object obj) {
    String text = String.valueOf(obj);
    SQLMethodInvokeExpr methodInvokeExpr = extractDateFunction(text);
    if (methodInvokeExpr != null) {
      return methodInvokeExpr;
    }
    return new SQLCharExpr(text);
  }

  private static final List<String> dateFunctionList = ArrayUtils.asArrayList("now", "sysdate", "current_timestamp");

  private SQLMethodInvokeExpr extractDateFunction(String text) {
    for (String dateFunction : dateFunctionList) {
      if (text.length() > dateFunction.length() && JdbcSQLUtils.hasPrefix(text, dateFunction)) {
        SQLExpr param = dataConvertorRegistry.parse(Integer.valueOf(text.substring(dateFunction.length() + 1, text.length() - 1)));
        return new SQLMethodInvokeExpr(dateFunction, null, param);
      }
    }
    return null;
  }

  @Override
  public SQLExpr doParse(String str) {
    return sqlCharExprDataConvertor.parse(str);
  }

  @Override
  public boolean strMatch(String str) {
    return sqlCharExprDataConvertor.strMatch(str);
  }

}
