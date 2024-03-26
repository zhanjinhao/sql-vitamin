package cn.addenda.sql.vitamins.rewriter.convertor.type;

import cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr.SQLNumberExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;

import java.math.BigDecimal;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class BigDecimalDataConvertor extends AbstractTypeDataConvertor<BigDecimal, SQLNumberExpr> {

  public BigDecimalDataConvertor() {
    super(new SQLNumberExprDataConvertor());
  }

  @Override
  public SQLNumberExpr doParse(Object obj) {
    BigDecimal bigDecimal = (BigDecimal) obj;
    return new SQLNumberExpr(bigDecimal);
  }

}
