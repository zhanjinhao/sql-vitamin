package cn.addenda.sql.vitamins.rewriter.dynamic.tablename;

import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.tablename.TableRenameVisitor;

/**
 * @author addenda
 * @since 2024/3/31 16:26
 */
public class DruidDynamicTableNameRewriter implements DynamicTableNameRewriter {

  @Override
  public String rename(String sql, String originalTableName, String targetTableName) {
    return DruidSQLUtils.statementMerge(sql, sqlStatement -> {
      TableRenameVisitor tableRenameVisitor = TableRenameVisitor.getInstance(originalTableName, targetTableName);
      sqlStatement.accept(tableRenameVisitor);
      return DruidSQLUtils.toLowerCaseSQL(sqlStatement);
    });
  }

}
