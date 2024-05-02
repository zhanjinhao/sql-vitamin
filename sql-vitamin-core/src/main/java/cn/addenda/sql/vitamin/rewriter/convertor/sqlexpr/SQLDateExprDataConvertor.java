package cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr;

import cn.addenda.sql.vitamin.rewriter.util.DateUtils;
import com.alibaba.druid.sql.ast.expr.SQLDateExpr;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author addenda
 * @since 2023/7/2 14:57
 */
public class SQLDateExprDataConvertor extends AbstractSQLExprDataConvertor<SQLDateExpr> {

  private static final Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

  private static final DateTimeFormatter DATE_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final ZoneId zoneId;

  public SQLDateExprDataConvertor(ZoneId zoneId) {
    this.zoneId = zoneId;
  }

  @Override
  public String format(Object obj) {
    SQLDateExpr parse = parse(obj);
    return SINGLE_QUOTATION + parse.getLiteral() + SINGLE_QUOTATION;
  }

  @Override
  public SQLDateExpr doParse(String str) {
    Matcher matcher = pattern.matcher(str);
    matcher.find();
    String group = matcher.group();
    return new SQLDateExpr(DateUtils.localDateToDate(LocalDate.parse(group, DATE_FORMATTER)), TimeZone.getTimeZone(zoneId));
  }

  @Override
  public boolean strMatch(String str) {
    return pattern.matcher(str).find();
  }

}
