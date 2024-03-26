package cn.addenda.sql.vitamins.client.spring.aop.sqlcheck;

import cn.addenda.sql.vitamins.client.spring.aop.NamedConfigurer;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.DruidSQLChecker;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SQLChecker;
import lombok.Getter;

/**
 * @author addenda
 * @since 2023/6/14 21:03
 */
public class SQLCheckConfigurer implements NamedConfigurer {

  @Getter
  private SQLChecker sqlChecker;

  public SQLCheckConfigurer() {
    this.sqlChecker = new DruidSQLChecker();
  }

}
