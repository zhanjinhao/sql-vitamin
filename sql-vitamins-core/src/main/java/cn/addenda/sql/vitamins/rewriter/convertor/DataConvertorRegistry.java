package cn.addenda.sql.vitamins.rewriter.convertor;

import com.alibaba.druid.sql.ast.SQLExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:52
 */
public interface DataConvertorRegistry {

  String format(Object obj);

  SQLExpr parse(Object obj);

  SQLExpr parse(String str, Class<?> clazz);

  boolean typeAvailable(Class<?> clazz);

}
