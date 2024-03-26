package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import cn.addenda.sql.vitamins.rewriter.util.DateUtils;
import com.alibaba.druid.sql.ast.expr.SQLDateTimeExpr;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLDateTimeExprDataConvertor extends AbstractSQLExprDataConvertor<SQLDateTimeExpr> {

  private static final Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final ZoneId zoneId;

  public SQLDateTimeExprDataConvertor(ZoneId zoneId) {
    this.zoneId = zoneId;
  }

  @Override
  public String format(Object obj) {
    SQLDateTimeExpr parse = parse(obj);
    return parse.getLiteral().toString();
  }

  @Override
  public SQLDateTimeExpr doParse(String str) {
    Matcher matcher = pattern.matcher(str);
    matcher.find();
    String group = matcher.group();
    return new SQLDateTimeExpr(DateUtils.localDateTimeToDate(LocalDateTime.parse(group, DATE_TIME_FORMATTER)), TimeZone.getTimeZone(zoneId));
  }

  @Override
  public boolean strMatch(String str) {
    return pattern.matcher(str).find();
  }

}
