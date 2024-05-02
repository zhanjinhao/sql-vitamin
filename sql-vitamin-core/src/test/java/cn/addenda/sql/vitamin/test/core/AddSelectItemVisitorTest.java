package cn.addenda.sql.vitamin.test.core;

import cn.addenda.sql.vitamin.rewriter.visitor.item.AddSelectItemVisitor;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author addenda
 * @since 2024/3/16 12:18
 */
public class AddSelectItemVisitorTest {

  @Test
  public void test1() {
    String sql = "select t_user_0.username\n" +
        "from t_user_0\n" +
        "         join (select username from t_user_1) t_user_1 on t_user_0.username = t_user_1.username";
    AddSelectItemVisitor addSelectItemVisitor = new AddSelectItemVisitor(sql, null, null, false, "t_user_1", "a");
    SQLSelectStatement visit = addSelectItemVisitor.visit();
    String expected = "SELECT t_user_0.username, t_user_0.a AS t_user_0_a, t_user_1.t_user_1_a AS a\n" +
        "FROM t_user_0\n" +
        "\tJOIN (\n" +
        "\t\tSELECT username, t_user_1.a AS t_user_1_a\n" +
        "\t\tFROM t_user_1\n" +
        "\t) t_user_1\n" +
        "\tON t_user_0.username = t_user_1.username";
    Assert.assertEquals(
        visit.toString().toLowerCase().replaceAll("\\s+", ""),
        expected.toLowerCase().replaceAll("\\s+", ""));
    Assert.assertEquals("a", addSelectItemVisitor.getResult());
  }

}
