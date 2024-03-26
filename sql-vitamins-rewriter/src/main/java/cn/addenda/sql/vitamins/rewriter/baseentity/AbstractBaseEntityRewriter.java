package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;

import java.util.List;

/**
 * @author addenda
 * @since 2023/5/14 16:40
 */
public abstract class AbstractBaseEntityRewriter implements BaseEntityRewriter {

  protected final BaseEntitySource baseEntitySource;

  /**
   * 需要基础字段的表
   */
  protected final List<String> included;

  /**
   * 不需要基础字段的表
   */
  protected final List<String> notIncluded;

  protected final DataConvertorRegistry dataConvertorRegistry;

  protected static final List<String> INSERT_COLUMN_NAME_LIST;
  protected static final List<String> INSERT_FIELD_NAME_LIST;
  protected static final List<String> UPDATE_COLUMN_NAME_LIST;
  protected static final List<String> UPDATE_FIELD_NAME_LIST;

  static {
    INSERT_COLUMN_NAME_LIST = BaseEntity.getAllColumnNameList();
    INSERT_FIELD_NAME_LIST = BaseEntity.getAllFieldNameList();
    UPDATE_COLUMN_NAME_LIST = BaseEntity.getUpdateColumnNameList();
    UPDATE_FIELD_NAME_LIST = BaseEntity.getUpdateFieldNameList();
  }

  protected AbstractBaseEntityRewriter(BaseEntitySource baseEntitySource,
                                       List<String> included, List<String> notIncluded, DataConvertorRegistry dataConvertorRegistry) {
    this.baseEntitySource = baseEntitySource;
    this.included = included;
    this.notIncluded = notIncluded;
    this.dataConvertorRegistry = dataConvertorRegistry;
  }
}
