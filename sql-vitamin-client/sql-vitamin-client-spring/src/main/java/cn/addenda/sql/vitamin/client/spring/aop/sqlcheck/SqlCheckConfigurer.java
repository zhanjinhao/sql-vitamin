package cn.addenda.sql.vitamin.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamin.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamin.rewriter.sqlcheck.DruidSqlChecker;
import cn.addenda.sql.vitamin.rewriter.sqlcheck.SqlChecker;
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
