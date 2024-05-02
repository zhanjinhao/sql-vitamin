package cn.addenda.sql.vitamin.test.rewriter.tombstone;

import cn.addenda.sql.vitamin.rewriter.tombstone.DruidTombstoneRewriter;
import cn.addenda.sql.vitamin.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamin.test.rewriter.SqlReader;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author addenda
 * @since 2023/5/10 20:39
 */
public class DruidTombstoneRewriterDeleteTest {

  private static String[] sqls = new String[]{
  };

  @Test
  public void test1() {
    for (String sql : SqlReader.read("src/test/resources/cn/addenda/sql/vitamin/test/rewriter/tombstone/TombstoneDelete.test", sqls)) {
      String source = sql;
      int i = source.lastIndexOf(";");
      sql = source.substring(0, i);
      String expect = source.substring(i + 1);
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
      if (sqlStatements.isEmpty()) {
        continue;
      }
      System.out.println("------------------------------------------------------------------------------------");
      DruidTombstoneRewriter druidTombstoneRewriter = new DruidTombstoneRewriter();
      String s = druidTombstoneRewriter.rewriteDeleteSql(DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)), false);
      sqlStatements = SQLUtils.parseStatements(s, DbType.mysql);
      List<SQLStatement> expectSqlStatements = SQLUtils.parseStatements(expect, DbType.mysql);
      Assert.assertEquals(DruidSQLUtils.toLowerCaseSQL(expectSqlStatements.get(0)).replaceAll("\\s+", ""),
          DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)).replaceAll("\\s+", ""));

    }
  }

}
