package cn.addenda.sql.vitamin.rewriter.visitor.condition;

import java.util.List;

/**
 * 先连接，再应用过滤条件。
 *
 * @author addenda
 * @since 2023/5/11 12:30
 */
public class ViewAddWhereConditionVisitor extends TableAddWhereConditionVisitor {

  public ViewAddWhereConditionVisitor(String condition) {
    super(condition);
  }

  public ViewAddWhereConditionVisitor(String tableName, String condition) {
    super(tableName, condition);
  }

  public ViewAddWhereConditionVisitor(List<String> included, List<String> excluded, String condition) {
    super(included, excluded, condition);
  }

  @Override
  protected String determineTableName(String tableName, String alias) {
    return alias == null ? tableName : alias;
  }
}
