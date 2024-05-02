package cn.addenda.sql.vitamin.rewriter.baseentity;

import lombok.SneakyThrows;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2023/5/2 19:36
 */
public interface BaseEntitySource {

  String getCreator();

  String getCreatorName();

  Object getCreateTime();

  String getModifier();

  String getModifierName();

  Object getModifyTime();

  String getRemark();

  @SneakyThrows
  default Object get(String fieldName) {
    String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    Method[] methods = this.getClass().getMethods();
    for (Method method : methods) {
      if (method.getName().equals(methodName)) {
        return method.invoke(this);
      }
    }
    return null;
  }

}
