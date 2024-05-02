package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLTimeExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLTimeExpr;

import java.sql.Time;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class SqlTimeDataConvertor extends AbstractTypeDataConvertor<Time, SQLTimeExpr> {

  public SqlTimeDataConvertor() {
    super(new SQLTimeExprDataConvertor(ZoneId.systemDefault()));
  }

  @Override
  public SQLTimeExpr doParse(Object obj) {
    return new SQLTimeExpr((Time) obj, TimeZone.getDefault());
  }

}
