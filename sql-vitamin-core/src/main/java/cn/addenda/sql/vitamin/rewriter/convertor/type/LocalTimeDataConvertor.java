package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLTimeExprDataConvertor;
import cn.addenda.sql.vitamin.rewriter.util.DateUtils;
import com.alibaba.druid.sql.ast.expr.SQLTimeExpr;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class LocalTimeDataConvertor extends AbstractTypeDataConvertor<LocalTime, SQLTimeExpr> {

  public LocalTimeDataConvertor() {
    super(new SQLTimeExprDataConvertor(ZoneId.systemDefault()));
  }

  @Override
  public SQLTimeExpr doParse(Object obj) {
    return new SQLTimeExpr(DateUtils.localTimeToDate((LocalTime) obj), TimeZone.getDefault());
  }

}
