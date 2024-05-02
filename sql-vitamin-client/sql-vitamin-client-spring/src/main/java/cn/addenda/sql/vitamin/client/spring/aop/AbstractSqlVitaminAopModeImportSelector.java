package cn.addenda.sql.vitamin.client.spring.aop;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

public abstract class AbstractSqlVitaminAopModeImportSelector<A extends Annotation> implements ImportSelector {

  public static final String SQL_VITAMIN_AOP_MODE_ATTRIBUTE_NAME = "sqlVitaminAopMode";

  protected String getSqlVitaminAopModeAttributeName() {
    return SQL_VITAMIN_AOP_MODE_ATTRIBUTE_NAME;
  }

  @Override
  public final String[] selectImports(AnnotationMetadata importingClassMetadata) {
    Class<?> annType = GenericTypeResolver.resolveTypeArgument(getClass(), AbstractSqlVitaminAopModeImportSelector.class);
    Assert.state(annType != null, "Unresolvable type argument for SqlVitaminAopModeImportSelector");
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(annType.getName(), false));
    if (attributes == null) {
      throw new IllegalArgumentException(String.format(
          "@%s is not present on importing class '%s' as expected",
          annType.getSimpleName(), importingClassMetadata.getClassName()));
    }

    SqlVitaminAopMode sqlVitaminAopMode = attributes.getEnum(getSqlVitaminAopModeAttributeName());
    String[] imports = selectImports(sqlVitaminAopMode);
    if (imports == null) {
      throw new IllegalArgumentException("Unknown SqlVitaminAopMode: " + sqlVitaminAopMode);
    }
    return imports;
  }

  @Nullable
  protected abstract String[] selectImports(SqlVitaminAopMode sqlVitaminAopMode);

}
