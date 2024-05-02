package cn.addenda.sql.vitamin.test.core;

import cn.addenda.sql.vitamin.rewriter.SqlVitaminException;
import cn.addenda.sql.vitamin.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.visitor.item.AddInsertItemVisitor;
import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.Item;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author addenda
 * @since 2024/3/16 11:37
 */
public class AddInsertItemVisitorTest {

  @Test
  public void test1() {
    String sql = "insert into t_user_0(username)\n" +
        "(\n" +
        "select username\n" +
        "from t_user_1\n" +
        "union\n" +
        "select username\n" +
        "from t_user_2)";
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
    MySqlInsertStatement sqlStatement1 = (MySqlInsertStatement) sqlStatements.get(0);
    SQLSelect query = sqlStatement1.getQuery();
    Assert.assertEquals(query.getQuery().getClass(), SQLUnionQuery.class);
  }

  @Test
  public void test2() {
    String sql = "insert into t_user_0(username)\n" +
        "select username\n" +
        "from t_user_1\n" +
        "union\n" +
        "select username\n" +
        "from t_user_2";
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
    MySqlInsertStatement sqlStatement1 = (MySqlInsertStatement) sqlStatements.get(0);
    SQLSelect query = sqlStatement1.getQuery();
    Assert.assertEquals(query.getQuery().getClass(), SQLUnionQuery.class);
  }

  @Test
  public void test3() {
    String sql = "insert into A(name) select name from B";
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
    MySqlInsertStatement sqlStatement1 = (MySqlInsertStatement) sqlStatements.get(0);
    SQLSelect query = sqlStatement1.getQuery();
    Assert.assertEquals(query.getQuery().getClass(), MySqlSelectQueryBlock.class);
  }

  @Test
  public void test4() {
    String sql = "insert into A(name) select name from B join C on b.cid = c.id";
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
    MySqlInsertStatement sqlStatement1 = (MySqlInsertStatement) sqlStatements.get(0);
    SQLSelect query = sqlStatement1.getQuery();
    Assert.assertEquals(query.getQuery().getClass(), MySqlSelectQueryBlock.class);
  }

  @Test
  public void test5() {
    String sql = "insert into t_user_0(username)\n" +
        "select t_user_1.username\n" +
        "from t_user_1\n" +
        "         join (select username from t_user_2) t_user_2 on t_user_1.username = t_user_2.username";
    AddInsertItemVisitor addSelectItemVisitor = new AddInsertItemVisitor(
        sql, Arrays.asList("t_user_1", "t_user_0"), null, new DefaultDataConvertorRegistry(),
        true, new Item("password", "a"),
        InsertAddSelectItemMode.DB, true, UpdateItemMode.ALL);
    MySqlInsertStatement visit = addSelectItemVisitor.visit();
    System.out.println(visit);
  }

  @Test
  public void test6() {
    String sql = "insert into t_user_0(username)\n" +
        "select t_user_1.username\n" +
        "from t_user_1\n" +
        "         join (select username from t_user_2) t_user_2 on t_user_1.username = t_user_2.username";
    AddInsertItemVisitor addSelectItemVisitor = new AddInsertItemVisitor(
        sql, Arrays.asList("t_user_1", "t_user_0", "t_user_2"), null, new DefaultDataConvertorRegistry(),
        true, new Item("password", "a"),
        InsertAddSelectItemMode.DB, true, UpdateItemMode.ALL);

    Assert.assertThrows(SqlVitaminException.class, () -> {
      try {
        MySqlInsertStatement visit = addSelectItemVisitor.visit();
        System.out.println(visit);
      } catch (SqlVitaminException e) {
        Assert.assertEquals(
            "无法从SQL中推断出来需要增加的itemName，SQL：[select t_user_1.username, t_user_1.password as t_user_1_password, t_user_2.t_user_2_password as t_user_2_t_user_2_password from t_user_1 join ( select username, t_user_2.password as t_user_2_password from t_user_2 ) t_user_2 on t_user_1.username = t_user_2.username]，item：[Item(itemName=password, itemValue=a)]。",
            e.getMessage()
        );
        throw e;
      }
    });
  }

}
