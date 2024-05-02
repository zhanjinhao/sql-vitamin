package cn.addenda.sql.vitamin.client.common.config;

import cn.addenda.sql.vitamin.client.common.annotation.ConfigBaseEntity;
import cn.addenda.sql.vitamin.client.common.constant.BoolConfig;
import cn.addenda.sql.vitamin.client.common.constant.InsertAddSelectItemModeConfig;
import cn.addenda.sql.vitamin.client.common.constant.Propagation;
import cn.addenda.sql.vitamin.client.common.constant.UpdateItemModeConfig;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityConfig;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntityContext;
import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author addenda
 * @since 2023/6/11 16:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseEntityConfigUtils {

  public static void configBaseEntity(Propagation propagation, BaseEntityConfig baseEntityConfig) {
    Propagation.assertNotNull(propagation);
    Boolean disable = baseEntityConfig.getDisable();
    if (disable != null) {
      Propagation.configWithPropagation(propagation, disable,
          BaseEntityContext::setDisable, BaseEntityContext::getDisable);
    }
    Boolean compatibleMode = baseEntityConfig.getCompatibleMode();
    if (compatibleMode != null) {
      Propagation.configWithPropagation(propagation, compatibleMode,
          BaseEntityContext::setCompatibleMode, BaseEntityContext::getCompatibleMode);
    }
    InsertAddSelectItemMode insertAddSelectItemMode = baseEntityConfig.getInsertAddSelectItemMode();
    if (insertAddSelectItemMode != null) {
      Propagation.configWithPropagation(propagation, insertAddSelectItemMode,
          BaseEntityContext::setInsertSelectAddItemMode, BaseEntityContext::getInsertSelectAddItemMode);
    }
    Boolean duplicateKeyUpdate = baseEntityConfig.getDuplicateKeyUpdate();
    if (duplicateKeyUpdate != null) {
      Propagation.configWithPropagation(propagation, duplicateKeyUpdate,
          BaseEntityContext::setDuplicateKeyUpdate, BaseEntityContext::getDuplicateKeyUpdate);
    }
    UpdateItemMode updateItemMode = baseEntityConfig.getUpdateItemMode();
    if (updateItemMode != null) {
      Propagation.configWithPropagation(propagation, updateItemMode,
          BaseEntityContext::setUpdateItemMode, BaseEntityContext::getUpdateItemMode);
    }
    String masterView = baseEntityConfig.getMasterView();
    if (masterView != null) {
      Propagation.configWithPropagation(propagation, masterView,
          BaseEntityContext::setMasterView, BaseEntityContext::getMasterView);
    }
  }

  public static void configBaseEntity(ConfigBaseEntity configBaseEntity) {
    configBaseEntity(
        configBaseEntity.propagation(),
        BaseEntityConfig.of(
            BoolConfig.toBoolean(configBaseEntity.disable()),
            BoolConfig.toBoolean(configBaseEntity.compatibleMode()),
            configBaseEntity.masterView(),
            BoolConfig.toBoolean(configBaseEntity.duplicateKeyUpdate()),
            InsertAddSelectItemModeConfig.toInsertAddSelectItemMode(configBaseEntity.insertAddSelectItemMode()),
            UpdateItemModeConfig.toUpdateItemMode(configBaseEntity.updateItemMode())));
  }

  public static void pushBaseEntity(Propagation propagation) {
    Propagation.assertNotNull(propagation);
    // NEW 压入新的
    if (propagation == Propagation.NEW || !BaseEntityContext.contextActive()) {
      BaseEntityContext.push(new BaseEntityConfig());
    }
    // MERGE_* 压入带参数的
    else {
      BaseEntityConfig baseEntityConfig = BaseEntityContext.peek();
      BaseEntityContext.push(new BaseEntityConfig(baseEntityConfig));
    }
  }

}
