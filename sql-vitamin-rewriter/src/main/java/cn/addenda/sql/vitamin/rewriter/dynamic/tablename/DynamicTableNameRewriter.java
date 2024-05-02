package cn.addenda.sql.vitamin.rewriter.dynamic.tablename;

/**
 * @author addenda
 * @since 2024/3/31 16:25
 */
public interface DynamicTableNameRewriter {

  String rename(String sql, String originalTableName, String targetTableName);

}
