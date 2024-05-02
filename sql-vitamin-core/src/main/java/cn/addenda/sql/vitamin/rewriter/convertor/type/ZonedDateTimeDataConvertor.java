package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLDateTimeExprDataConvertor;
import cn.addenda.sql.vitamin.rewriter.util.DateUtils;
import com.alibaba.druid.sql.ast.expr.SQLDateTimeExpr;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 * @author addenda
 * @since 2022/9/11 17:49
 */
public class ZonedDateTimeDataConvertor extends AbstractTypeDataConvertor<ZonedDateTime, SQLDateTimeExpr> {

  private final ZoneId zoneId;

  public ZonedDateTimeDataConvertor(ZoneId zoneId) {
    super(new SQLDateTimeExprDataConvertor(zoneId));
    this.zoneId = zoneId;
  }

  @Override
  public SQLDateTimeExpr doParse(Object obj) {
    ZonedDateTime zonedDateTime = (ZonedDateTime) obj;
    // 计算逻辑，获取到zonedDateTime的时间戳，再转为zoneId对应的localDateTime
    // 转换前后的两个对象表示的同一个时间：
    // 假如传入的UTC+9的10点，配置的zoneId是UTC+7，那么对UTC+7这个时区来说就是8点，即返回值为8点。
    LocalDateTime localDateTime = LocalDateTime.ofInstant(zonedDateTime.toInstant(), zoneId);
    return new SQLDateTimeExpr(DateUtils.localDateTimeToDate(localDateTime), TimeZone.getDefault());
  }

}
