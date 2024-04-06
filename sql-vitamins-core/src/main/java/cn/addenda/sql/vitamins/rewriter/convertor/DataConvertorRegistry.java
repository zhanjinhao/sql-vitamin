package cn.addenda.sql.vitamins.rewriter.convertor;

import com.alibaba.druid.sql.ast.SQLExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:52
 */
public interface DataConvertorRegistry {

  /**
   * Object -> String
   */
  String format(Object obj);

  /**
   * Object -> SQLExpr
   */
  SQLExpr parse(Object obj);

  /**
   * String -> SQLExpr
   */
  SQLExpr parse(String str, Class<?> clazz);

  boolean typeAvailable(Class<?> clazz);

}
