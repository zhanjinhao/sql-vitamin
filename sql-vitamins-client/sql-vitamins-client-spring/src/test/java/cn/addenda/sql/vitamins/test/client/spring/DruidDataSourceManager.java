package cn.addenda.sql.vitamins.test.client.spring;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

public class DruidDataSourceManager {

  private static final String URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "root";

  public static DataSource getDataSource(String dbname) {
    DruidDataSource druidDataSource = new DruidDataSource();
    druidDataSource.setUrl(String.format(URL, dbname));
    druidDataSource.setUsername(USERNAME);
    druidDataSource.setPassword(PASSWORD);
    druidDataSource.setTestWhileIdle(false);

    return druidDataSource;
  }

}