package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLBigIntExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLBigIntExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class LongDataConvertor extends AbstractTypeDataConvertor<Long, SQLBigIntExpr> {

  public LongDataConvertor() {
    super(new SQLBigIntExprDataConvertor());
  }

  @Override
  public SQLBigIntExpr doParse(Object obj) {
    return new SQLBigIntExpr((Long) obj);
  }

}
