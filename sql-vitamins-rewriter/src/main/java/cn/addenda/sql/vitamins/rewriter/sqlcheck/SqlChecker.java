package cn.addenda.sql.vitamins.rewriter.sqlcheck;

/**
 * @author addenda
 * @since 2023/5/7 19:56
 */
public interface SqlChecker {

  boolean exactIdentifier(String sql);

  boolean allColumnExists(String sql);

  boolean dmlConditionExists(String sql);
}
