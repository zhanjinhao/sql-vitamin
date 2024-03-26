package cn.addenda.sql.vitamins.rewriter.convertor.type;

import cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr.SQLIntegerExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class IntegerDataConvertor extends AbstractTypeDataConvertor<Integer, SQLIntegerExpr> {

  public IntegerDataConvertor() {
    super(new SQLIntegerExprDataConvertor());
  }

  @Override
  public SQLIntegerExpr doParse(Object obj) {
    return new SQLIntegerExpr((Integer) obj);
  }

}
