package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLBooleanExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class BooleanDataConvertor extends AbstractTypeDataConvertor<Boolean, SQLBooleanExpr> {

  public BooleanDataConvertor() {
    super(new SQLBooleanExprDataConvertor());
  }

  @Override
  public SQLBooleanExpr doParse(Object obj) {
    return new SQLBooleanExpr((Boolean) obj);
  }

}
