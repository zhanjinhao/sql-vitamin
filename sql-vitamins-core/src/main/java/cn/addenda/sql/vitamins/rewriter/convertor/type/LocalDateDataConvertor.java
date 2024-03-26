package cn.addenda.sql.vitamins.rewriter.convertor.type;

import cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr.SQLDateExprDataConvertor;
import cn.addenda.sql.vitamins.rewriter.util.DateUtils;
import com.alibaba.druid.sql.ast.expr.SQLDateExpr;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class LocalDateDataConvertor extends AbstractTypeDataConvertor<LocalDate, SQLDateExpr> {

  public LocalDateDataConvertor() {
    super(new SQLDateExprDataConvertor(ZoneId.systemDefault()));
  }

  @Override
  public SQLDateExpr doParse(Object obj) {
    return new SQLDateExpr(DateUtils.localDateToDate((LocalDate) obj), TimeZone.getDefault());
  }

}
