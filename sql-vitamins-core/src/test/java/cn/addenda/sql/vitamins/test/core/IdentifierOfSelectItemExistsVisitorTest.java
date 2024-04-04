package cn.addenda.sql.vitamins.test.core;

import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.identifier.IdentifierOfSelectItemExistsVisitor;
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
public class IdentifierOfSelectItemExistsVisitorTest {

  private static String[] sqls = new String[]{
  };

  @Test
  public void test1() {
    String[] read = SqlReader.read("src/test/resources/cn/addenda/sql/vitamins/test/core/IdentifierOfSelectItemExistsVisitor.test", sqls);
    for (int line = 0; line < read.length; line++) {
      String sql = read[line];
      String source = sql;
      int i = source.lastIndexOf(";");
      sql = source.substring(0, i);
      boolean flag = Boolean.parseBoolean(source.substring(i + 1));
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
      if (sqlStatements.isEmpty()) {
        continue;
      }
      System.out.println(line + " : ------------------------------------------------------------------------------------");
      System.out.println();
      IdentifierOfSelectItemExistsVisitor identifierExistsVisitor = new IdentifierOfSelectItemExistsVisitor(sql, "a", null, ArrayUtils.asArrayList("dual"));
      identifierExistsVisitor.visit();
      boolean exists = identifierExistsVisitor.isExists();
      System.out.println(identifierExistsVisitor.getFlagStack());
      Assert.assertEquals("[]", identifierExistsVisitor.getFlagStack().toString());
      Assert.assertEquals(flag, exists);
    }
  }

}
