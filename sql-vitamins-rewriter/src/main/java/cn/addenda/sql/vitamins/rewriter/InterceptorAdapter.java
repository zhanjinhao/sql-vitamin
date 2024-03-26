package cn.addenda.sql.vitamins.rewriter;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

import java.sql.SQLException;

/**
 * @author addenda
 * @since 2023/4/23 19:12
 */
public class InterceptorAdapter extends FilterAdapter implements Interceptor {

  @Override
  public ConnectionProxy dataSource_getConnection(
    InterceptorChain chain, InterceptedDataSource dataSource)
    throws SQLException {
    return chain.dataSource_connect(dataSource);
  }

  @Override
  public ConnectionProxy dataSource_getConnection(
    InterceptorChain chain, InterceptedDataSource dataSource, String username, String password)
    throws SQLException {
    return chain.dataSource_connect(dataSource, username, password);
  }

  @Override
  public void dataSource_releaseConnection(
    FilterChain chain, DruidPooledConnection connection)
    throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public DruidPooledConnection dataSource_getConnection(
    FilterChain chain, DruidDataSource dataSource, long maxWaitMillis)
    throws SQLException {
    throw new UnsupportedOperationException();
  }

}
