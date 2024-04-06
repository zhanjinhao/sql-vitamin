package cn.addenda.sql.vitamins.rewriter.convertor;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;
import com.alibaba.druid.sql.ast.SQLExpr;

import java.lang.reflect.ParameterizedType;

/**
 * <ul>
 *   <li>String format(Object obj): Object -> MySQL String</li>
 *   <li>R parse(Object obj): Object -> SQLExpr</li>
 *   <li>R parse(String str): Java String -> SQLExpr</li>
 * </ul>
 *
 * @author addenda
 * @since 2023/6/9 21:39
 */
public abstract class AbstractDataConvertor<T, R extends SQLExpr> implements DataConvertor<T, R> {

  @Override
  public Class<T> getType() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<T>) parameterizedType.getActualTypeArguments()[0];
  }

  @Override
  public R parse(Object obj) {
    assertType(obj);
    return doParse(obj);
  }

  private void assertType(Object o) {
    if (o == null) {
      throw new SqlVitaminsException(String.format("[%s] 不支持 null。", getClass().getName()));
    }
    if (!getType().isAssignableFrom(o.getClass())) {
      String msg = String.format("类型不匹配，obj：[%s]，actual type：[%s]，expected type：[%s]。", o, o.getClass().getName(), getType().getName());
      throw new SqlVitaminsException(msg);
    }
  }

  /**
   * @param obj 一定是 {@link DataConvertor#getType()} 的对象
   */
  public abstract R doParse(Object obj);

  @Override
  public R parse(String str) {
    if (str == null) {
      throw new SqlVitaminsException(String.format("[%s] 不支持 null。", getClass().getName()));
    }
    if (!strMatch(str)) {
      String msg = String.format("类型不匹配，value：[%s]，期望类型：[%s]。", str, getType().getName());
      throw new SqlVitaminsException(msg);
    }
    return doParse(str);
  }

  /**
   * @param str 不为null
   */
  public abstract boolean strMatch(String str);

  public abstract R doParse(String str);

}
