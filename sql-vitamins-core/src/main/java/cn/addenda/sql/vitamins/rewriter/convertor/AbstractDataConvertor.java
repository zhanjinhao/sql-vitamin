package cn.addenda.sql.vitamins.rewriter.convertor;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;
import com.alibaba.druid.sql.ast.SQLExpr;

import java.lang.reflect.ParameterizedType;

/**
 * @author addenda
 * @since 2023/6/9 21:39
 */
public abstract class AbstractDataConvertor<T, R extends SQLExpr> implements DataConvertor<T, R> {

  @Override
  public String format(Object obj) {
    return parse(obj).toString();
  }

  @Override
  public R parse(Object obj) {
    assertType(obj);
    return doParse(obj);
  }

  protected void assertType(Object o) {
    if (o == null) {
      throw new SqlVitaminsException(DataConvertor.class.getName() + " 不支持 null。");
    }
    if (!getType().isAssignableFrom(o.getClass())) {
      String msg = String.format("类型不匹配，value：[%s]，当前类型：[%s]，期望类型：[%s]。", o, o.getClass().getName(), getType().getName());
      throw new SqlVitaminsException(msg);
    }
  }

  @Override
  public R parse(String str) {
    if (str == null) {
      throw new SqlVitaminsException(DataConvertor.class.getName() + " 不支持 null。");
    }
    if (!strMatch(str)) {
      String msg = String.format("类型不匹配，value：[%s]，期望类型：[%s]。", str, getType().getName());
      throw new SqlVitaminsException(msg);
    }
    return doParse(str);
  }

  /**
   * @param obj 一定是 {@link DataConvertor#getType()} 的对象
   */
  public abstract R doParse(Object obj);

  public abstract R doParse(String str);

  /**
   * @param str 不为null
   */
  public abstract boolean strMatch(String str);

  @Override
  public Class<T> getType() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<T>) parameterizedType.getActualTypeArguments()[0];
  }

}
