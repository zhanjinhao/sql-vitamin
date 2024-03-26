package cn.addenda.sql.vitamins.client.common.annotation;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/10 13:53
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UnCheckExactIdentifier {
  boolean value() default true;
}
