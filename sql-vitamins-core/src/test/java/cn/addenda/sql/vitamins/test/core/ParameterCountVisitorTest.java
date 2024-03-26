package cn.addenda.sql.vitamins.test.core;

import cn.addenda.sql.vitamins.rewriter.visitor.identifier.ParameterCountVisitor;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author addenda
 * @since 2023/6/15 21:08
 */
public class ParameterCountVisitorTest {

  @Test
  public void test() {
    String sql = "select * from a where a.name = ? and a.age = ?";
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
    ParameterCountVisitor parameterCountVisitor = new ParameterCountVisitor(sqlStatements.get(0));
    parameterCountVisitor.visit();
    Assert.assertEquals(parameterCountVisitor.getParameterCount(), 2);
  }

}
