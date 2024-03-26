package cn.addenda.sql.vitamins.rewriter;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author addenda
 * @since 2023/4/23 18:58
 */
public class InterceptedDataSource extends WrapperAdapter implements DataSource {

  private DataSource dataSource;

  @Setter
  @Getter
  private InnerDataSourceProxyImpl dataSourceProxy;

  public InterceptedDataSource(DataSource dataSource, Driver rawDriver, DataSourceProxyConfig config, List<Interceptor> interceptorList) {
    this.dataSource = dataSource;
    if (interceptorList == null) {
      throw new IllegalArgumentException("interceptorList不能为空。");
    }
    dataSourceProxy = new InnerDataSourceProxyImpl(rawDriver, config, interceptorList);
  }

  public InterceptedDataSource(DataSource dataSource, List<Interceptor> interceptorList) {
    this.dataSource = dataSource;
    if (interceptorList == null) {
      throw new IllegalArgumentException("interceptorList不能为空。");
    }
    dataSourceProxy = new InnerDataSourceProxyImpl(interceptorList);
  }

  @Override
  public Connection getConnection() throws SQLException {
    InterceptorChain filterChain = new InterceptorChainImpl(dataSourceProxy);
    return filterChain.dataSource_connect(this);
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    InterceptorChain filterChain = new InterceptorChainImpl(dataSourceProxy);
    return filterChain.dataSource_connect(this, username, password);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return dataSource.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    dataSource.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    dataSource.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return dataSource.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return dataSource.getParentLogger();
  }

  public ConnectionProxy getConnectionDirect() throws SQLException {
    return new ConnectionProxyImpl(
      dataSourceProxy, dataSource.getConnection(), new Properties(), dataSourceProxy.createConnectionId());
  }

  public ConnectionProxy getConnectionDirect(String username, String password) throws SQLException {
    return new ConnectionProxyImpl(
      dataSourceProxy, dataSource.getConnection(username, password), new Properties(), dataSourceProxy.createConnectionId());
  }

  public static class InnerDataSourceProxyImpl extends DataSourceProxyImpl {

    @Getter
    private final List<Interceptor> interceptorList;

    private final List<Filter> filterList;

    public InnerDataSourceProxyImpl(Driver rawDriver, DataSourceProxyConfig config, List<Interceptor> interceptorList) {
      super(rawDriver, config);
      interceptorList.sort(Comparator.comparing(Interceptor::order));
      this.interceptorList = interceptorList;
      this.filterList = new ArrayList<>(interceptorList);
    }

    public InnerDataSourceProxyImpl(List<Interceptor> interceptorList) {
      super(new Driver() {

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
          throw new UnsupportedOperationException();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
          throw new UnsupportedOperationException();
        }

        @Override
        public int getMajorVersion() {
          return -1;
        }

        @Override
        public int getMinorVersion() {
          return -1;
        }

        @Override
        public boolean jdbcCompliant() {
          throw new UnsupportedOperationException();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
          throw new UnsupportedOperationException();
        }
      }, new DataSourceProxyConfig());
      interceptorList.sort(Comparator.comparing(Interceptor::order));
      this.interceptorList = interceptorList;
      this.filterList = new ArrayList<>(interceptorList);
    }

    @Override
    public List<Filter> getProxyFilters() {
      return filterList;
    }

    @Override
    public String[] getFilterClasses() {
      List<Filter> filterConfigList = getProxyFilters();

      List<String> classes = new ArrayList<>();
      for (Filter filter : filterConfigList) {
        classes.add(filter.getClass().getName());
      }

      return classes.toArray(new String[classes.size()]);
    }

  }

}
