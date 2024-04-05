package cn.addenda.sql.vitamins.client.mybatis.helper;

import cn.addenda.sql.vitamins.client.common.annotation.*;
import cn.addenda.sql.vitamins.client.common.annotation.ConfigBaseEntity;
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

  private final Map<String, ConfigBaseEntity> disableBaseEntityMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigDynamicSuffix> configDynamicSuffixMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigTombstone> disableTombstoneMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigSqlCheck> configSqlCheckMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigDynamicCondition> configDynamicConditionMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigDynamicItem> configDynamicItemMap = new ConcurrentHashMap<>();
  private final Map<String, ConfigDynamicTableName> configDynamicTableNameMap = new ConcurrentHashMap<>();

  private final Set<String> suffixSet;

  protected AbstractMsIdExtractHelper(Set<String> suffixSet) {
    this.suffixSet = suffixSet;
  }

  protected AbstractMsIdExtractHelper() {
    suffixSet = null;
  }

  @Override
  public ConfigBaseEntity extractConfigBaseEntity(String msId) {
    return disableBaseEntityMap.computeIfAbsent(msId,
        s -> extract(msId, ConfigBaseEntity.class));
  }

  @Override
  public ConfigDynamicSuffix extractConfigDynamicSuffix(String msId) {
    return configDynamicSuffixMap.computeIfAbsent(msId,
        s -> extract(msId, ConfigDynamicSuffix.class));
  }

  @Override
  public ConfigTombstone extractConfigTombstone(String msId) {
    return disableTombstoneMap.computeIfAbsent(msId,
        s -> extract(msId, ConfigTombstone.class));
  }

  @Override
  public ConfigSqlCheck extractConfigSqlCheck(String msId) {
    return configSqlCheckMap.computeIfAbsent(msId,
        s -> extract(msId, ConfigSqlCheck.class));
  }

  @Override
  public ConfigDynamicCondition extractConfigDynamicCondition(String msId) {
    return configDynamicConditionMap.computeIfAbsent(msId,
        s -> extract(msId, ConfigDynamicCondition.class));
  }

  @Override
  public ConfigDynamicItem extractConfigDynamicItem(String msId) {
    return configDynamicItemMap.computeIfAbsent(msId,
        s -> extract(msId, ConfigDynamicItem.class));
  }

  @Override
  public ConfigDynamicTableName extractConfigDynamicTableName(String msId) {
    return configDynamicTableNameMap.computeIfAbsent(msId,
        s -> extract(msId, ConfigDynamicTableName.class));
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
