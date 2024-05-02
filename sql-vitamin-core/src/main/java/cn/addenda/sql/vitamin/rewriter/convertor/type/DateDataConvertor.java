package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLDateTimeExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLDateTimeExpr;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class DateDataConvertor extends AbstractTypeDataConvertor<Date, SQLDateTimeExpr> {

  public DateDataConvertor() {
    super(new SQLDateTimeExprDataConvertor(ZoneId.systemDefault()));
  }

  @Override
  public SQLDateTimeExpr doParse(Object obj) {
    return new SQLDateTimeExpr((Date) obj, TimeZone.getDefault());
  }

}
