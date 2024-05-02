package cn.addenda.sql.vitamin.test.rewriter.tombstone;

import cn.addenda.sql.vitamin.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.tombstone.DruidTombstoneRewriter;
import cn.addenda.sql.vitamin.rewriter.util.ArrayUtils;
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
public class DruidTombstoneRewriterSelectUseSubQueryTest {

  private static String[] sqls = new String[]{
  };

  @Test
  public void test1() {
    String[] read = SqlReader.read("src/test/resources/cn/addenda/sql/vitamin/test/rewriter/tombstone/TombstoneSelectUseSubQuery.test", sqls);
    for (int line = 0; line < read.length; line++) {
      String sql = read[line];
      String source = sql;
      int i = source.lastIndexOf(";");
      sql = source.substring(0, i);
      String expect = source.substring(i + 1);
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
      if (sqlStatements.isEmpty()) {
        continue;
      }
      System.out.println(line + " : ------------------------------------------------------------------------------------");
      DruidTombstoneRewriter druidTombstoneRewriter = new DruidTombstoneRewriter(null, ArrayUtils.asArrayList("dual"), new DefaultDataConvertorRegistry());
      String s = druidTombstoneRewriter.rewriteSelectSql(DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)), true);
      sqlStatements = SQLUtils.parseStatements(s, DbType.mysql);

      System.out.println("actual: " + s);
      System.out.println("expect: " + expect);
      List<SQLStatement> expectSqlStatements = SQLUtils.parseStatements(expect, DbType.mysql);
//            Assert.assertEquals(DruidSQLUtils.toLowerCaseSQL(expectSqlStatements.get(0)).replaceAll("\\s+", ""),
//                DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)).replaceAll("\\s+", ""));
      Assert.assertEquals(expectSqlStatements.get(0), sqlStatements.get(0));

    }
  }

}
