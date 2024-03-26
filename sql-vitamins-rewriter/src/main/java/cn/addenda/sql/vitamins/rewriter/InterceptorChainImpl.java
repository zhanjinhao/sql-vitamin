package cn.addenda.sql.vitamins.rewriter;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;

import java.sql.SQLException;

/**
 * @author addenda
 * @since 2023/4/23 19:01
 */
public class InterceptorChainImpl extends FilterChainImpl implements InterceptorChain {

  public InterceptorChainImpl(DataSourceProxy dataSource) {
    super(dataSource);
  }

  public InterceptorChainImpl(DataSourceProxy dataSource, int pos) {
    super(dataSource, pos);
  }

  @Override
  public FilterChain cloneChain() {
    return new InterceptorChainImpl(getDataSource(), pos);
  }

  @Override
  public ConnectionProxy dataSource_connect(
    InterceptedDataSource dataSource) throws SQLException {
    if (this.pos < getFilterSize()) {
      int curPos = getPos();
      setPos(curPos + 1);
      return ((Interceptor) getFilters().get(curPos))
        .dataSource_getConnection(this, dataSource);
    }

    return dataSource.getConnectionDirect();
  }

  @Override
  public ConnectionProxy dataSource_connect(
    InterceptedDataSource dataSource, String username, String password)
    throws SQLException {
    if (this.pos < getFilterSize()) {
      int curPos = getPos();
      setPos(curPos + 1);
      return ((Interceptor) getFilters().get(curPos))
        .dataSource_getConnection(this, dataSource, username, password);
    }

    return dataSource.getConnectionDirect(username, password);
  }

  @Override
  public void dataSource_recycle(
    DruidPooledConnection connection) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public DruidPooledConnection dataSource_connect(
    DruidDataSource dataSource, long maxWaitMillis)
    throws SQLException {
    throw new UnsupportedOperationException();
  }

  public void setPos(int pos) {
    this.pos = pos;
  }

}
