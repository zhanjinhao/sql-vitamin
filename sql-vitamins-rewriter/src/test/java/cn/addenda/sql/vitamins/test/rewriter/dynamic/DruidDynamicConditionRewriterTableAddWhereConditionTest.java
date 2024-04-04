package cn.addenda.sql.vitamins.test.rewriter.dynamic;

import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DruidDynamicConditionRewriter;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionRewriter;
import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.test.rewriter.SqlReader;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author addenda
 * @since 2023/5/13 12:41
 */
@Slf4j
public class DruidDynamicConditionRewriterTableAddWhereConditionTest {

  private static String[] sqls = new String[]{
  };

  @Test
  public void test1() {
    String[] read = SqlReader.read("src/test/resources/cn/addenda/sql/vitamins/test/rewriter/dynamic/DynamicSQLRewriterTableAddWhereCondition.test", sqls);
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
      DynamicConditionRewriter druidDynamicConditionRewriter = new DruidDynamicConditionRewriter(ArrayUtils.asArrayList("dual"));
      String s = druidDynamicConditionRewriter.tableAddWhereCondition(DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)), null, "if_del=0");
      sqlStatements = SQLUtils.parseStatements(s, DbType.mysql);
      List<SQLStatement> expectSqlStatements = SQLUtils.parseStatements(expect, DbType.mysql);

      SQLStatement expectStatement = expectSqlStatements.get(0);
      SQLStatement actualStatement = sqlStatements.get(0);
      if (expectStatement instanceof SQLInsertStatement || expectStatement instanceof SQLDeleteStatement) {
        Assert.assertEquals(
            DruidSQLUtils.toLowerCaseSQL(expectStatement),
            DruidSQLUtils.toLowerCaseSQL(actualStatement));
      } else {
        Assert.assertEquals(
            expectStatement,
            actualStatement);
      }

    }
  }

}
