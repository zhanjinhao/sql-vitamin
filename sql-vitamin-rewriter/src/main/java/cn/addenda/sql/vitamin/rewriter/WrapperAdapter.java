package cn.addenda.sql.vitamin.rewriter;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * copy from sharding-jdbc project
 *
 * @author addenda
 * @since 2023/4/16 11:34
 */
public class WrapperAdapter implements Wrapper {

  /**
   * 当前对象强转为 iface。
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (isWrapperFor(iface)) {
      return (T) this;
    }
    throw new SQLException(String.format("[%s] cannot be unwrapped as [%s]", getClass().getName(), iface.getName()));
  }

  /**
   * 当前对象是否是 iface 的实例。<br/>
   * 是： true <br/>
   * 否： false
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return iface.isInstance(this);
  }

}
