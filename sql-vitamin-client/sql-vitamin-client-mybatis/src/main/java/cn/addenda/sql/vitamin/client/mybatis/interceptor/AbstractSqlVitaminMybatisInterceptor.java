package cn.addenda.sql.vitamin.client.mybatis.interceptor;

import cn.addenda.sql.vitamin.client.mybatis.helper.DefaultMsIdExtractHelper;
import cn.addenda.sql.vitamin.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamin.rewriter.SqlVitaminException;
import lombok.Setter;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

/**
 * @author addenda
 * @since 2023/6/11 12:19
 */
public abstract class AbstractSqlVitaminMybatisInterceptor implements Interceptor {

  @Setter
  protected MsIdExtractHelper msIdExtractHelper;

  protected AbstractSqlVitaminMybatisInterceptor(MsIdExtractHelper msIdExtractHelper) {
    this.msIdExtractHelper = msIdExtractHelper;
  }

  protected AbstractSqlVitaminMybatisInterceptor() {
  }

  @Override
  public Object plugin(Object target) {
    // 兼容低版本 mybatis
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {
    if (msIdExtractHelper != null) {
      String aMsIdExtractHelper = (String) properties.get("msIdExtractHelper");

      if (aMsIdExtractHelper != null) {
        Class<? extends MsIdExtractHelper> aClass;
        try {
          aClass = (Class<? extends MsIdExtractHelper>) Class.forName(aMsIdExtractHelper);
        } catch (Exception e) {
          String msg = String.format("找不到类，MsIdExtractHelper初始化失败：[%s]。", aMsIdExtractHelper);
          throw new SqlVitaminException(msg, e);
        }

        this.msIdExtractHelper = newInstance(aClass);
      } else {
        this.msIdExtractHelper = DefaultMsIdExtractHelper.getInstance();
      }
    }
  }

  private <T> T newInstance(Class<? extends T> aClass) {
    try {
      Method[] methods = aClass.getMethods();
      for (Method method : methods) {
        if (method.getName().equals("getInstance") && Modifier.isStatic(method.getModifiers()) &&
            method.getParameterCount() == 0) {
          return (T) method.invoke(null);
        }
      }
      return aClass.newInstance();
    } catch (Exception e) {
      throw new SqlVitaminException("创建对象异常，MsIdExtractHelper初始化失败：" + aClass.getName(), e);
    }
  }

}
