package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLSmallIntExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLSmallIntExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class ShortDataConvertor extends AbstractTypeDataConvertor<Short, SQLSmallIntExpr> {

  public ShortDataConvertor() {
    super(new SQLSmallIntExprDataConvertor());
  }

  @Override
  public SQLSmallIntExpr doParse(Object obj) {
    return new SQLSmallIntExpr((Short) obj);
  }

}
