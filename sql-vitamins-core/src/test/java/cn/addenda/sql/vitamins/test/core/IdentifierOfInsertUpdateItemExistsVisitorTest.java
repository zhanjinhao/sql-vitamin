package cn.addenda.sql.vitamins.test.core;

import cn.addenda.sql.vitamins.rewriter.visitor.identifier.IdentifierOfInsertUpdateItemExistsVisitor;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author addenda
 * @since 2023/5/13 21:24
 */
public class IdentifierOfInsertUpdateItemExistsVisitorTest {

  private static String[] sqls = new String[]{

  };

  @Test
  public void test1() {
    for (String sql : SqlReader.read("src/test/resources/cn/addenda/sql/vitamins/test/core/IdentifierOfInsertUpdateItemExistsVisitor.test", sqls)) {
      String source = sql;
      int i = source.lastIndexOf(";");
      sql = source.substring(0, i);
      boolean flag = Boolean.parseBoolean(source.substring(i + 1));
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
      if (sqlStatements.size() == 0) {
        continue;
      }
      System.out.println("------------------------------------------------------------------------------------");
      System.out.println();
      IdentifierOfInsertUpdateItemExistsVisitor identifierExistsVisitor = new IdentifierOfInsertUpdateItemExistsVisitor(sql, "a");
      identifierExistsVisitor.visit();
      boolean exists = identifierExistsVisitor.isExists();
      Assert.assertEquals(flag, exists);
    }
  }

}
