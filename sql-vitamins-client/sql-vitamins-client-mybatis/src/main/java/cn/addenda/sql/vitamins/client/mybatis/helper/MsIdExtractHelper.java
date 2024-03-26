package cn.addenda.sql.vitamins.client.mybatis.helper;

import cn.addenda.sql.vitamins.client.common.annotation.*;

/**
 * @author addenda
 * @since 2023/6/11 12:26
 */
public interface MsIdExtractHelper {

  ConfigPropagation extractConfigPropagation(String msId);

  DisableBaseEntity extractDisableBaseEntity(String msId);

  ConfigMasterView extractConfigMasterView(String msId);

  ConfigDuplicateKeyUpdate extractConfigDuplicateKeyUpdate(String msId);

  ConfigUpdateItemMode extractConfigUpdateItemMode(String msId);

  ConfigReportItemNameExists extractConfigReportItemNameExists(String msId);

  ConfigInsertSelectAddItemMode extractConfigInsertSelectAddItemMode(String msId);

  DynamicConditions extractDynamicConditions(String msId);

  DynamicItems extractDynamicItems(String msId);

  ConfigDupThenNew extractConfigDupThenNew(String msId);

  ConfigJoinUseSubQuery extractConfigJoinUseSubQuery(String msId);

  ConfigLock extractConfigLock(String msId);

  UnCheckAllColumn extractUnCheckAllColumn(String msId);

  UnCheckDmlCondition extractUnCheckDmlCondition(String msId);

  UnCheckExactIdentifier extractUnCheckExactIdentifier(String msId);

  DisableTombstone extractDisableTombstone(String msId);

}
