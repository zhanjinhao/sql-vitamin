package cn.addenda.sql.vitamins.client.mybatis.helper;

import cn.addenda.sql.vitamins.client.common.annotation.*;
import cn.addenda.sql.vitamins.client.common.annotation.ConfigBaseEntity;

/**
 * @author addenda
 * @since 2023/6/11 12:26
 */
public interface MsIdExtractHelper {

  ConfigBaseEntity extractConfigBaseEntity(String msId);

  ConfigDynamicSuffix extractConfigDynamicSuffix(String msId);

  ConfigTombstone extractConfigTombstone(String msId);

  ConfigSqlCheck extractConfigSqlCheck(String msId);

  ConfigDynamicCondition extractConfigDynamicCondition(String msId);

  ConfigDynamicItem extractConfigDynamicItem(String msId);

  ConfigDynamicTableName extractConfigDynamicTableName(String msId);

}
