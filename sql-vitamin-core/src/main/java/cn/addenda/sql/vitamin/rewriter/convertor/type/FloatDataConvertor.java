package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLNumberExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class FloatDataConvertor extends AbstractTypeDataConvertor<Float, SQLNumberExpr> {

  public FloatDataConvertor() {
    super(new SQLNumberExprDataConvertor());
  }

  @Override
  public SQLNumberExpr doParse(Object obj) {
    return new SQLNumberExpr((Float) obj);
  }

}
