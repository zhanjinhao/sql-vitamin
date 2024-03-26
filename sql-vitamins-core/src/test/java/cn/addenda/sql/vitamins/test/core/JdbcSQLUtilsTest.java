package cn.addenda.sql.vitamins.test.core;

import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author addenda
 * @since 2024/3/22 21:19
 */
public class JdbcSQLUtilsTest {

  @Test
  public void test1() {
    Assert.assertEquals("column", JdbcSQLUtils.extractColumnName("column"));
    Assert.assertEquals("column", JdbcSQLUtils.extractColumnName("table.column"));
    Assert.assertEquals("column", JdbcSQLUtils.extractColumnName("schema.table.column"));
  }

  @Test
  public void test2() {
    Assert.assertEquals(null, JdbcSQLUtils.extractColumnOwner("column"));
    Assert.assertEquals("table", JdbcSQLUtils.extractColumnOwner("table.column"));
    Assert.assertEquals("table", JdbcSQLUtils.extractColumnOwner("schema.table.column"));
  }

}
