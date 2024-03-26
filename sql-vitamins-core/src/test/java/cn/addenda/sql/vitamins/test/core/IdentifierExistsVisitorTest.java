package cn.addenda.sql.vitamins.test.core;

import cn.addenda.sql.vitamins.rewriter.visitor.identifier.IdentifierExistsVisitor;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author addenda
 * @since 2023/5/3 20:55
 */
public class IdentifierExistsVisitorTest {

  private static String[] sqls = new String[]{
  };

  @Test
  public void test1() {
    String[] read = SqlReader.read("src/test/resources/cn/addenda/sql/vitamins/test/core/IdentifierExistsVisitor.test", sqls);
    for (int line = 0; line < read.length; line++) {
      String source = read[line];
      int i = source.lastIndexOf(";");
      String sql = source.substring(0, i);
      boolean flag = Boolean.parseBoolean(source.substring(i + 1));
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
      if (sqlStatements.isEmpty()) {
        continue;
      }
      System.out.println(line + " : ------------------------------------------------------------------------------------");
      System.out.println();
      IdentifierExistsVisitor identifierExistsVisitor = new IdentifierExistsVisitor(sql, "a");
      identifierExistsVisitor.visit();
      boolean exists = identifierExistsVisitor.isExists();
      Assert.assertEquals(flag, exists);
    }
  }

  @Test
  public void test2() {
    String[] read = new String[]{
        " select a , b  from tab2  t cross join tab3   left join tab3  on tab4.e  = tab2.e    ,  (  select *  from tab5   )  t5  where t.m  = ?  and  exists  (  select 1 from tab4  t4  where t1.n  = t4.n   )   and t.tm  >= '2016-11-11';true"
    };
    for (int line = 0; line < read.length; line++) {
      String source = read[line];
      int i = source.lastIndexOf(";");
      String sql = source.substring(0, i);
      boolean flag = Boolean.parseBoolean(source.substring(i + 1));
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
      if (sqlStatements.isEmpty()) {
        continue;
      }
      IdentifierExistsVisitor identifierExistsVisitor = new IdentifierExistsVisitor(sql, "a", Arrays.asList("tab2"), null);
      identifierExistsVisitor.visit();
      boolean exists = identifierExistsVisitor.isExists();
      Assert.assertEquals(flag, exists);
    }
  }

  @Test
  public void test3() {
    String[] read = new String[]{
        " select tab3.a , b  from tab2  t cross join tab3   left join tab3  on tab4.e  = tab2.e    ,  (  select *  from tab5   )  t5  where t.m  = ?  and  exists  (  select 1 from tab4  t4  where t1.n  = t4.n   )   and t.tm  >= '2016-11-11';false"
    };
    for (int line = 0; line < read.length; line++) {
      String source = read[line];
      int i = source.lastIndexOf(";");
      String sql = source.substring(0, i);
      boolean flag = Boolean.parseBoolean(source.substring(i + 1));
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
      if (sqlStatements.isEmpty()) {
        continue;
      }
      IdentifierExistsVisitor identifierExistsVisitor = new IdentifierExistsVisitor(sql, "a", Arrays.asList("tab2"), null);
      identifierExistsVisitor.visit();
      boolean exists = identifierExistsVisitor.isExists();
      Assert.assertEquals(flag, exists);
    }
  }

}
