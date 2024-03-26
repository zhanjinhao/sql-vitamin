package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import cn.addenda.sql.vitamins.rewriter.util.DateUtils;
import com.alibaba.druid.sql.ast.expr.SQLTimeExpr;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLTimeExprDataConvertor extends AbstractSQLExprDataConvertor<SQLTimeExpr> {

  private static final Pattern pattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");

  private static final DateTimeFormatter TIME_FORMATTER =
    DateTimeFormatter.ofPattern("HH:mm:ss");

  private final ZoneId zoneId;

  public SQLTimeExprDataConvertor(ZoneId zoneId) {
    this.zoneId = zoneId;
  }

  @Override
  public String format(Object obj) {
    SQLTimeExpr parse = parse(obj);
    return parse.getLiteral().toString();
  }

  @Override
  public SQLTimeExpr doParse(String str) {
    Matcher matcher = pattern.matcher(str);
    matcher.find();
    String group = matcher.group();
    return new SQLTimeExpr(DateUtils.localTimeToDate(LocalTime.parse(group, TIME_FORMATTER)), TimeZone.getTimeZone(zoneId));
  }

  @Override
  public boolean strMatch(String str) {
    return pattern.matcher(str).find();
  }

}
