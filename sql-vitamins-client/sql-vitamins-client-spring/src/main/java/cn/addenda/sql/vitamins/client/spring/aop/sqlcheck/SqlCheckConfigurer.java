package cn.addenda.sql.vitamins.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.DruidSqlChecker;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SqlChecker;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/6/14 21:03
 */
public class SqlCheckConfigurer implements NamedConfigurer {

  @Getter
  private SqlChecker sqlChecker;

  public SqlCheckConfigurer() {
    this.sqlChecker = new DruidSqlChecker();
  }

  public SqlCheckConfigurer(SqlChecker sqlChecker) {
    this.sqlChecker = sqlChecker;
  }

}
