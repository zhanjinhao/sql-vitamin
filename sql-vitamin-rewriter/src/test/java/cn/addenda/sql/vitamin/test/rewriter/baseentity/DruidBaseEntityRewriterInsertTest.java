package cn.addenda.sql.vitamin.test.rewriter.baseentity;

import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityRewriter;
import cn.addenda.sql.vitamin.rewriter.baseentity.DefaultBaseEntitySource;
import cn.addenda.sql.vitamin.rewriter.baseentity.DruidBaseEntityRewriter;
import cn.addenda.sql.vitamin.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamin.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
import cn.addenda.sql.vitamin.test.rewriter.SqlReader;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author addenda
 * @since 2023/5/14 17:07
 */
public class DruidBaseEntityRewriterInsertTest {

  private static String[] sqls = new String[]{
  };

  @Test
  public void test1() {
    String[] read = SqlReader.read(
        "src/test/resources/cn/addenda/sql/vitamin/test/rewriter/baseentity/BaseEntityInsert.test", sqls);
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
      BaseEntityRewriter baseEntityRewriter = new DruidBaseEntityRewriter(null, ArrayUtils.asArrayList("dual"), new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
      String s = baseEntityRewriter.rewriteInsertSql(DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)),
          InsertAddSelectItemMode.DB, false, UpdateItemMode.NOT_NULL);
      sqlStatements = SQLUtils.parseStatements(s, DbType.mysql);
      List<SQLStatement> expectSqlStatements = SQLUtils.parseStatements(expect, DbType.mysql);
      Assert.assertEquals(
          DruidSQLUtils.toLowerCaseSQL(expectSqlStatements.get(0)).replaceAll("\\s+", " "),
          DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)).replaceAll("\\s+", " "));
    }
  }


  @Test
  public void test2() {
    String sql = "insert  into score ( SNO, CNO, DEGREE ) values ( 109, '3-105', DEGREE  + 76  )  on duplicate key update SNO=131, CNO='4-111', DEGREE=DEGREE_MAX  + 1;insert into score (SNO, CNO, DEGREE, creator, creator_name, create_time, modifier, modifier_name, modify_time, remark) values (109, '3-105', DEGREE + 76, 'addenda', 'addenda', now(3), 'addenda', 'addenda', now(3), null) on duplicate key update SNO = 131, CNO    = '4-111',  DEGREE = DEGREE_MAX + 1,  modifier = 'addenda', modifier_name = 'addenda', modify_time = now(3)";
    String source = sql;
    int i = source.lastIndexOf(";");
    sql = source.substring(0, i);
    String expect = source.substring(i + 1);
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
    if (sqlStatements.size() == 0) {
      return;
    }
    BaseEntityRewriter baseEntityRewriter = new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
    String s = baseEntityRewriter.rewriteInsertSql(DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)),
        InsertAddSelectItemMode.DB, true, UpdateItemMode.NOT_NULL);
    sqlStatements = SQLUtils.parseStatements(s, DbType.mysql);
    List<SQLStatement> expectSqlStatements = SQLUtils.parseStatements(expect, DbType.mysql);
    Assert.assertEquals(
        DruidSQLUtils.toLowerCaseSQL(expectSqlStatements.get(0)).replaceAll("\\s+", " "),
        DruidSQLUtils.toLowerCaseSQL(sqlStatements.get(0)).replaceAll("\\s+", " "));


  }
}