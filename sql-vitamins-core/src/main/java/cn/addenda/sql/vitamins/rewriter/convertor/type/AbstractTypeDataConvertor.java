package cn.addenda.sql.vitamins.rewriter.convertor.type;

import cn.addenda.sql.vitamins.rewriter.convertor.AbstractDataConvertor;
import cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr.AbstractSQLExprDataConvertor;
import com.alibaba.druid.sql.ast.SQLExpr;

/**
 * @author addenda
 * @since 2023/7/2 15:53
 */
public abstract class AbstractTypeDataConvertor<T, R extends SQLExpr> extends AbstractDataConvertor<T, R> {

  private final AbstractSQLExprDataConvertor<R> formatter;

  protected AbstractTypeDataConvertor(AbstractSQLExprDataConvertor<R> formatter) {
    this.formatter = formatter;
  }

  @Override
  public String format(Object obj) {
    R parse = parse(obj);
    return formatter.format(parse);
  }

  @Override
  public R doParse(String str) {
    return formatter.doParse(str);
  }

  @Override
  public boolean strMatch(String str) {
    return formatter.strMatch(str);
  }

}
