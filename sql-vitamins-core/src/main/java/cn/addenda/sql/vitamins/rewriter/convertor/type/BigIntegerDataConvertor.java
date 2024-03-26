package cn.addenda.sql.vitamins.rewriter.convertor.type;

import cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr.SQLIntegerExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;

import java.math.BigInteger;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class BigIntegerDataConvertor extends AbstractTypeDataConvertor<BigInteger, SQLIntegerExpr> {

  public BigIntegerDataConvertor() {
    super(new SQLIntegerExprDataConvertor());
  }

  @Override
  public SQLIntegerExpr doParse(Object obj) {
    BigInteger bigInteger = (BigInteger) obj;
    return new SQLIntegerExpr(bigInteger);
  }

}
