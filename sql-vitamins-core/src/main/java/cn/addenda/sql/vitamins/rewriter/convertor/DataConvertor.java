package cn.addenda.sql.vitamins.rewriter.convertor;

import com.alibaba.druid.sql.ast.SQLExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:51
 */
public interface DataConvertor<T, R extends SQLExpr> {

  String SINGLE_QUOTATION = "'";

  Class<T> getType();

  String format(Object obj);

  R parse(Object obj);

  R parse(String str);

}
