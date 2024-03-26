package cn.addenda.sql.vitamins.rewriter.convertor.sqlexpr;

import cn.addenda.sql.vitamins.rewriter.convertor.AbstractDataConvertor;
import com.alibaba.druid.sql.ast.SQLExpr;

/**
 * @author addenda
 * @since 2023/6/10 10:54
 */
public abstract class AbstractSQLExprDataConvertor<T extends SQLExpr> extends AbstractDataConvertor<T, T> {

  protected AbstractSQLExprDataConvertor() {
  }

  @Override
  public final T doParse(Object obj) {
    return (T) obj;
  }

}
