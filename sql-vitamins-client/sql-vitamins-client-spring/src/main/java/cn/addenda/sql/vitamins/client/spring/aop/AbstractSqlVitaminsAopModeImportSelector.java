package cn.addenda.sql.vitamins.client.spring.aop;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

public abstract class AbstractSqlVitaminsAopModeImportSelector<A extends Annotation> implements ImportSelector {

  public static final String SQL_VITAMINS_AOP_MODE_ATTRIBUTE_NAME = "sqlVitaminsAopMode";

  protected String getSqlVitaminsAopModeAttributeName() {
    return SQL_VITAMINS_AOP_MODE_ATTRIBUTE_NAME;
  }

  @Override
  public final String[] selectImports(AnnotationMetadata importingClassMetadata) {
    Class<?> annType = GenericTypeResolver.resolveTypeArgument(getClass(), AbstractSqlVitaminsAopModeImportSelector.class);
    Assert.state(annType != null, "Unresolvable type argument for SqlVitaminsAopModeImportSelector");
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(annType.getName(), false));
    if (attributes == null) {
      throw new IllegalArgumentException(String.format(
        "@%s is not present on importing class '%s' as expected",
        annType.getSimpleName(), importingClassMetadata.getClassName()));
    }

    SqlVitaminsAopMode sqlVitaminsAopMode = attributes.getEnum(getSqlVitaminsAopModeAttributeName());
    String[] imports = selectImports(sqlVitaminsAopMode);
    if (imports == null) {
      throw new IllegalArgumentException("Unknown SqlVitaminsAopMode: " + sqlVitaminsAopMode);
    }
    return imports;
  }

  @Nullable
  protected abstract String[] selectImports(SqlVitaminsAopMode sqlVitaminsAopMode);

}
