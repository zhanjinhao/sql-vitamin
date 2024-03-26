package cn.addenda.sql.vitamins.rewriter;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

import java.sql.SQLException;

/**
 * @author addenda
 * @since 2023/4/23 18:57
 */
public interface Interceptor extends Filter {

  int MAX = Integer.MAX_VALUE;

  ConnectionProxy dataSource_getConnection(
    InterceptorChain chain, InterceptedDataSource dataSource)
    throws SQLException;

  ConnectionProxy dataSource_getConnection(
    InterceptorChain chain, InterceptedDataSource dataSource, String username, String password)
    throws SQLException;

  default int order() {
    return MAX / 2 - 60000;
  }

}
