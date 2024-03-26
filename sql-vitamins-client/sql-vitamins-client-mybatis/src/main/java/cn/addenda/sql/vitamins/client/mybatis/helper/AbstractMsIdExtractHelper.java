package cn.addenda.sql.vitamins.client.mybatis.helper;

import cn.addenda.sql.vitamins.client.common.annotation.*;
import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntityException;
import cn.addenda.sql.vitamins.rewriter.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author addenda
 * @since 2023/6/11 12:28
 */
public abstract class AbstractMsIdExtractHelper implements MsIdExtractHelper {

  private final Map<String, ConfigPropagation> configPropagationMap = new ConcurrentHashMap<>();
  private final Map<String, DisableBaseEntity> disableBaseEntityMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigMasterView> configMasterViewMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigReportItemNameExists> configReportItemNameExistsMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigInsertSelectAddItemMode> configInsertSelectAddItemModeMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigUpdateItemMode> configUpdateItemModeMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigDuplicateKeyUpdate> configDuplicateKeyUpdateMap = new ConcurrentHashMap<>();
  private final Map<String, DynamicConditions> dynamicConditionsMap = new ConcurrentHashMap<>();
  private final Map<String, DynamicItems> dynamicItemsMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigDupThenNew> stringConfigDupThenNewMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigJoinUseSubQuery> configJoinUseSubQueryMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigLock> configLockMap = new ConcurrentHashMap<>();
  private final Map<String, UnCheckAllColumn> unCheckAllColumnMap = new ConcurrentHashMap<>();
  private final Map<String, UnCheckDmlCondition> unCheckDmlConditionMap = new ConcurrentHashMap<>();
  private final Map<String, UnCheckExactIdentifier> unCheckExactIdentifierMap = new ConcurrentHashMap<>();
  private final Map<String, DisableTombstone> disableTombstoneMap = new ConcurrentHashMap<>();

  private final Set<String> suffixSet;

  protected AbstractMsIdExtractHelper(Set<String> suffixSet) {
    this.suffixSet = suffixSet;
  }

  protected AbstractMsIdExtractHelper() {
    suffixSet = null;
  }

  @Override
  public ConfigPropagation extractConfigPropagation(String msId) {
    return configPropagationMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigPropagation.class));
  }

  @Override
  public DisableBaseEntity extractDisableBaseEntity(String msId) {
    return disableBaseEntityMap.computeIfAbsent(msId,
      s -> extract(msId, DisableBaseEntity.class));
  }

  @Override
  public ConfigMasterView extractConfigMasterView(String msId) {
    return configMasterViewMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigMasterView.class));
  }

  @Override
  public ConfigDuplicateKeyUpdate extractConfigDuplicateKeyUpdate(String msId) {
    return configDuplicateKeyUpdateMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigDuplicateKeyUpdate.class));
  }

  @Override
  public ConfigUpdateItemMode extractConfigUpdateItemMode(String msId) {
    return configUpdateItemModeMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigUpdateItemMode.class));
  }

  @Override
  public ConfigReportItemNameExists extractConfigReportItemNameExists(String msId) {
    return configReportItemNameExistsMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigReportItemNameExists.class));
  }

  @Override
  public ConfigInsertSelectAddItemMode extractConfigInsertSelectAddItemMode(String msId) {
    return configInsertSelectAddItemModeMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigInsertSelectAddItemMode.class));
  }

  @Override
  public DynamicConditions extractDynamicConditions(String msId) {
    return dynamicConditionsMap.computeIfAbsent(msId,
      s -> extract(msId, DynamicConditions.class));
  }

  @Override
  public DynamicItems extractDynamicItems(String msId) {
    return dynamicItemsMap.computeIfAbsent(msId,
      s -> extract(msId, DynamicItems.class));
  }

  @Override
  public ConfigDupThenNew extractConfigDupThenNew(String msId) {
    return stringConfigDupThenNewMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigDupThenNew.class));
  }

  @Override
  public ConfigJoinUseSubQuery extractConfigJoinUseSubQuery(String msId) {
    return configJoinUseSubQueryMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigJoinUseSubQuery.class));
  }

  @Override
  public ConfigLock extractConfigLock(String msId) {
    return configLockMap.computeIfAbsent(msId,
      s -> extract(msId, ConfigLock.class));
  }

  @Override
  public UnCheckAllColumn extractUnCheckAllColumn(String msId) {
    return unCheckAllColumnMap.computeIfAbsent(msId,
      s -> extract(msId, UnCheckAllColumn.class));
  }

  @Override
  public UnCheckDmlCondition extractUnCheckDmlCondition(String msId) {
    return unCheckDmlConditionMap.computeIfAbsent(msId,
      s -> extract(msId, UnCheckDmlCondition.class));
  }

  @Override
  public UnCheckExactIdentifier extractUnCheckExactIdentifier(String msId) {
    return unCheckExactIdentifierMap.computeIfAbsent(msId,
      s -> extract(msId, UnCheckExactIdentifier.class));
  }

  @Override
  public DisableTombstone extractDisableTombstone(String msId) {
    return disableTombstoneMap.computeIfAbsent(msId,
      s -> extract(msId, DisableTombstone.class));
  }

  protected <T extends Annotation> T extract(String msId, Class<T> tClass) {
    msId = removeSuffix(msId);
    int end = msId.lastIndexOf(".");
    try {
      Class<?> aClass = Class.forName(msId.substring(0, end));
      String methodName = msId.substring(end + 1);
      return AnnotationUtils.extractAnnotationFromMethod(aClass, methodName, tClass);
    } catch (ClassNotFoundException e) {
      String msg = String.format("无法找到对应的Mapper：[%s]。", msId);
      throw new BaseEntityException(msg, e);
    }
  }

  private String removeSuffix(String msId) {
    if (suffixSet == null) {
      return msId;
    }
    for (String suffix : suffixSet) {
      int suffixLength = suffix.length();
      int msIdLength = msId.length();
      if (suffixLength <= msIdLength) {
        boolean fg = true;
        for (int i = 1; i <= suffixLength; i++) {
          if (suffix.charAt(suffixLength - i) != msId.charAt(msIdLength - i)) {
            fg = false;
            break;
          }
        }
        if (fg) {
          msId = msId.substring(0, msIdLength - suffixLength);
          // 只能匹配中一次suffix
          break;
        }
      }
    }
    return msId;
  }

}
