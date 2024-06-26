package cn.addenda.sql.vitamin.rewriter.visitor.item;

import cn.addenda.sql.vitamin.rewriter.SqlVitaminException;
import cn.addenda.sql.vitamin.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamin.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamin.rewriter.visitor.SqlBoundVisitor;
import cn.addenda.sql.vitamin.rewriter.visitor.ViewToTableVisitor;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;

import java.util.List;

/**
 * <ul>
 *   <li>select：select column</li>
 *   <li>update: set</li>
 *   <li>insert: values 或 duplicateKeyUpdate</li>
 * </ul>
 *
 * @author addenda
 * @since 2023/5/17 18:12
 */
public abstract class AbstractAddItemVisitor<T extends SQLStatement> extends SqlBoundVisitor<T> {

  protected final List<String> included;

  protected final List<String> excluded;

  protected AbstractAddItemVisitor(String sql, List<String> included, List<String> excluded) {
    super(sql);
    this.included = included;
    this.excluded = excluded;
  }

  protected AbstractAddItemVisitor(T sqlStatement, List<String> included, List<String> excluded) {
    super(sqlStatement);
    this.included = included;
    this.excluded = excluded;
  }

  @Override
  public T visit() {
    sqlStatement.accept(ViewToTableVisitor.getInstance());
    sqlStatement.accept(this);
    return sqlStatement;
  }

  /**
   * @return 是否注入item
   */
  protected boolean checkItemNameExists(
      SQLObject x, List<SQLExpr> columnList, String itemName, boolean reportItemNameExists) {
    for (SQLExpr column : columnList) {
      if (JdbcSQLUtils.extractColumnName(DruidSQLUtils.toLowerCaseSQL(column)).equalsIgnoreCase(itemName)) {
        if (reportItemNameExists) {
          String msg = String.format("SQLObject：[%s]中已经存在[%s]，无法新增！", DruidSQLUtils.toLowerCaseSQL(x), itemName);
          throw new SqlVitaminException(msg);
        } else {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "AbstractAddItemVisitor{" +
        ", included=" + included +
        ", excluded=" + excluded +
        "} " + super.toString();
  }
}
