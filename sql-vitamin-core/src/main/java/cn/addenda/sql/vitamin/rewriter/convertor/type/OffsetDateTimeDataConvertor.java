package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLDateTimeExprDataConvertor;
import cn.addenda.sql.vitamin.rewriter.util.DateUtils;
import com.alibaba.druid.sql.ast.expr.SQLDateTimeExpr;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author addenda
 * @since 2022/9/11 17:49
 */
public class OffsetDateTimeDataConvertor extends AbstractTypeDataConvertor<OffsetDateTime, SQLDateTimeExpr> {

  private final ZoneId zoneId;

  public OffsetDateTimeDataConvertor(ZoneId zoneId) {
    super(new SQLDateTimeExprDataConvertor(zoneId));
    this.zoneId = zoneId;
  }

  @Override
  public SQLDateTimeExpr doParse(Object obj) {
    OffsetDateTime offsetDateTime = (OffsetDateTime) obj;
    LocalDateTime localDateTime = LocalDateTime.ofInstant(offsetDateTime.toInstant(), zoneId);
    return new SQLDateTimeExpr(DateUtils.localDateTimeToDate(localDateTime), TimeZone.getDefault());
  }

}
