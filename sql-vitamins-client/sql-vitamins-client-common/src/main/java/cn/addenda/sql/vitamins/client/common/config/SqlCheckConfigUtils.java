package cn.addenda.sql.vitamins.client.common.config;

import cn.addenda.sql.vitamins.client.common.annotation.ConfigSqlCheck;
import cn.addenda.sql.vitamins.client.common.constant.BoolConfig;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SqlCheckConfig;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SqlCheckContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author addenda
 * @since 2023/6/11 16:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlCheckConfigUtils {

  public static void configSqlCheck(Propagation propagation, SqlCheckConfig sqlCheckConfig) {
    Propagation.assertNotNull(propagation);
    Boolean disable = sqlCheckConfig.getDisable();
    if (disable != null) {
      Propagation.configWithPropagation(propagation, disable,
          SqlCheckContext::setDisable, SqlCheckContext::getDisable);
    }
    Boolean checkAllColumn = sqlCheckConfig.getCheckAllColumn();
    if (checkAllColumn != null) {
      Propagation.configWithPropagation(propagation, checkAllColumn,
          SqlCheckContext::setCheckAllColumn, SqlCheckContext::getCheckAllColumn);
    }
    Boolean checkExactIdentifier = sqlCheckConfig.getCheckExactIdentifier();
    if (checkExactIdentifier != null) {
      Propagation.configWithPropagation(propagation, checkExactIdentifier,
          SqlCheckContext::setCheckExactIdentifier, SqlCheckContext::getCheckExactIdentifier);
    }
    Boolean checkDmlCondition = sqlCheckConfig.getCheckDmlCondition();
    if (checkDmlCondition != null) {
      Propagation.configWithPropagation(propagation, checkDmlCondition,
          SqlCheckContext::setCheckDmlCondition, SqlCheckContext::getCheckDmlCondition);
    }
  }

  public static void configSqlCheck(ConfigSqlCheck configSqlCheck) {
    Propagation propagation = configSqlCheck.propagation();
    configSqlCheck(propagation,
        SqlCheckConfig.of(
            BoolConfig.toBoolean(configSqlCheck.disable()),
            BoolConfig.toBoolean(configSqlCheck.checkAllColumn()),
            BoolConfig.toBoolean(configSqlCheck.checkExactIdentifier()),
            BoolConfig.toBoolean(configSqlCheck.checkDmlCondition())
        ));
  }

  public static void pushSqlCheck(Propagation propagation) {
    Propagation.assertNotNull(propagation);
    // NEW 压入新的
    if (propagation == Propagation.NEW || !SqlCheckContext.contextActive()) {
      SqlCheckContext.push(new SqlCheckConfig());
    }
    // MERGE_* 压入带参数的
    else {
      SqlCheckConfig sqlCheckConfig = SqlCheckContext.peek();
      SqlCheckContext.push(new SqlCheckConfig(sqlCheckConfig));
    }
  }

}
