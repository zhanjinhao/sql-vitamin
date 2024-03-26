package cn.addenda.sql.vitamins.rewriter.convertor.type;

import cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr.SQLDateExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLDateExpr;

import java.sql.Date;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class SqlDateDataConvertor extends AbstractTypeDataConvertor<Date, SQLDateExpr> {

  public SqlDateDataConvertor() {
    super(new SQLDateExprDataConvertor(ZoneId.systemDefault()));
  }

  @Override
  public SQLDateExpr doParse(Object obj) {
    return new SQLDateExpr((Date) obj, TimeZone.getDefault());
  }

}
