package cn.addenda.sql.vitamins.rewriter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Slf4j
public class SqlRewritableDataSource extends WrapperAdapter implements DataSource {

  private final DataSource delegate;

  @Getter
  private final SqlRewriter sqlRewriter;

  public SqlRewritableDataSource(DataSource delegate, SqlRewriter sqlRewriter) {
    this.delegate = delegate;
    this.sqlRewriter = sqlRewriter;
  }

  @Override
  public Connection getConnection() throws SQLException {
    Connection connection = delegate.getConnection();
    return new SqlRewritableConnection(this, connection, sqlRewriter);
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    Connection connection = delegate.getConnection(username, password);
    return new SqlRewritableConnection(this, connection, sqlRewriter);
  }

  protected PrintWriter logWriter = new PrintWriter(System.out);

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return logWriter;
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    this.logWriter = out;
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    DriverManager.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return DriverManager.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }

}

