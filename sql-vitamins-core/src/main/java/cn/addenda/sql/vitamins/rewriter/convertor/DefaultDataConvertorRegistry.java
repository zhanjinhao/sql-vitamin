package cn.addenda.sql.vitamins.rewriter.convertor;

import cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr.*;
import cn.addenda.sql.vitamins.rewriter.convertor.type.*;

import java.time.ZoneId;

/**
 * @author addenda
 * @since 2022/9/10 20:09
 */
public class DefaultDataConvertorRegistry extends AbstractDataConvertorRegistry {

  public DefaultDataConvertorRegistry(ZoneId zoneId) {
    super(zoneId);
  }

  public DefaultDataConvertorRegistry() {
    super();
  }

  public static DefaultDataConvertorRegistry getInstance() {
    return new DefaultDataConvertorRegistry();
  }

  public void init() {
    addDataConvertor(new SQLBigIntExprDataConvertor());
    addDataConvertor(new SQLBooleanExprDataConvertor());
    addDataConvertor(new SQLCharExprDataConvertor());
    addDataConvertor(new SQLDateExprDataConvertor(zoneId));
    addDataConvertor(new SQLDateTimeExprDataConvertor(zoneId));
    addDataConvertor(new SQLDoubleExprDataConvertor());
    addDataConvertor(new SQLFloatExprDataConvertor());
    addDataConvertor(new SQLIntegerExprDataConvertor());
    addDataConvertor(new SQLNumberExprDataConvertor());
    addDataConvertor(new SQLSmallIntExprDataConvertor());
    addDataConvertor(new SQLTimeExprDataConvertor(zoneId));
    addDataConvertor(new SQLTinyIntExprDataConvertor());
    addDataConvertor(new SQLNullExprDataConvertor());

    addDataConvertor(new BooleanDataConvertor());
    addDataConvertor(new ByteDataConvertor());
    addDataConvertor(new ShortDataConvertor());
    addDataConvertor(new IntegerDataConvertor());
    addDataConvertor(new LongDataConvertor());
    addDataConvertor(new BigIntegerDataConvertor());
    addDataConvertor(new FloatDataConvertor());
    addDataConvertor(new DoubleDataConvertor());
    addDataConvertor(new BigDecimalDataConvertor());
    addDataConvertor(new CharacterDataConvertor());
    addDataConvertor(new CharSequenceDataConvertor(this));
    addDataConvertor(new DateDataConvertor());
    addDataConvertor(new SqlTimeDataConvertor());
    addDataConvertor(new SqlDateDataConvertor());
    addDataConvertor(new LocalTimeDataConvertor());
    addDataConvertor(new LocalDateTimeDataConvertor());
    addDataConvertor(new LocalDateDataConvertor());
    addDataConvertor(new OffsetDateTimeDataConvertor(zoneId));
    addDataConvertor(new ZonedDateTimeDataConvertor(zoneId));
  }

}
