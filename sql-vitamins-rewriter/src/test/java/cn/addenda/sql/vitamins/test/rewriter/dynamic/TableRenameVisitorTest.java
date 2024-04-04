package cn.addenda.sql.vitamins.test.rewriter.dynamic;

import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.tablename.TableRenameVisitor;
import cn.addenda.sql.vitamins.test.rewriter.SqlReader;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author addenda
 * @since 2024/3/14 9:13
 */
public class TableRenameVisitorTest {

  private static String[] sqls = new String[]{
  };

  @Test
  public void test1() {
    String[] read = SqlReader.read("src/test/resources/cn/addenda/sql/vitamins/test/rewriter/dynamic/TableRenameVisitorTest.test", sqls);
    for (int line = 0; line < read.length; line++) {
      String sql = read[line];
      String source = sql;
      int i = source.lastIndexOf(";");
      sql = source.substring(0, i);
      String expect = source.substring(i + 1);
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
      if (sqlStatements.isEmpty()) {
        continue;
      }
      System.out.println(line + " : ------------------------------------------------------------------------------------");
      SQLStatement sqlStatement = sqlStatements.get(0);
      MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
      sqlStatement.accept(visitor);
      Map<Name, TableStat> tables = visitor.getTables();
      Set<Name> names = tables.keySet();
      System.out.println(names);
      for (Name name : names) {
        String tableName = name.getName();
        if (tableName.contains(".")) {
          tableName = tableName.substring(tableName.indexOf(".") + 1);
        }
        TableRenameVisitor tableRenameVisitor = TableRenameVisitor.getInstance(
            tableName.replace("`", ""),
            tableName.replace("`", "") + "1");
        sqlStatement.accept(tableRenameVisitor);
      }
      Assert.assertEquals(expect.toUpperCase().replaceAll("\\s+", ""),
          DruidSQLUtils.toLowerCaseSQL(sqlStatement, false).toUpperCase().replaceAll("\\s+", ""));

    }
  }

}