package cn.addenda.sql.vitamins.rewriter.convertor;

import com.alibaba.druid.sql.ast.SQLExpr;

/**
 * <ul>
 *   <li>String format(Object obj): Object -> MySQL String</li>
 *   <li>R parse(Object obj): Object -> SQLExpr</li>
 *   <li>R parse(String str): Java String -> SQLExpr</li>
 * </ul>
 *
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
