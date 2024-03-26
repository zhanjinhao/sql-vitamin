package cn.addenda.sql.vitamins.client.common;

import cn.addenda.sql.vitamins.client.common.annotation.*;
import cn.addenda.sql.vitamins.client.common.constant.Propagation;
import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntityContext;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicConditionOperation;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicItemOperation;
import cn.addenda.sql.vitamins.rewriter.dynamicsql.DynamicSQLContext;
import cn.addenda.sql.vitamins.rewriter.lockingreads.LockingReadsContext;
import cn.addenda.sql.vitamins.rewriter.pojo.Ternary;
import cn.addenda.sql.vitamins.rewriter.sqlcheck.SQLCheckContext;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneContext;
import cn.addenda.sql.vitamins.rewriter.visitor.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author addenda
 * @since 2023/6/11 16:42
 */
public class ConfigContextUtils {

  private ConfigContextUtils() {
  }

  public static void configBaseEntity(
    Propagation propagation, DisableBaseEntity disableBaseEntity,
    ConfigMasterView configMasterView, ConfigDuplicateKeyUpdate configDuplicateKeyUpdate,
    ConfigUpdateItemMode configUpdateItemMode, ConfigReportItemNameExists configReportItemNameExists,
    ConfigInsertSelectAddItemMode configInsertSelectAddItemMode) {
    if (disableBaseEntity != null) {
      Propagation.configWithPropagation(propagation, disableBaseEntity.value(),
        BaseEntityContext::setDisable, BaseEntityContext::getDisable);
    }

    if (configMasterView != null) {
      Propagation.configWithPropagation(propagation, configMasterView.value(),
        BaseEntityContext::setMasterView, BaseEntityContext::getMasterView);
    }

    if (configDuplicateKeyUpdate != null) {
      Propagation.configWithPropagation(propagation, configDuplicateKeyUpdate.value(),
        BaseEntityContext::setDuplicateKeyUpdate, BaseEntityContext::getDuplicateKeyUpdate);
    }

    if (configUpdateItemMode != null) {
      Propagation.configWithPropagation(propagation, configUpdateItemMode.value(),
        BaseEntityContext::setUpdateItemMode, BaseEntityContext::getUpdateItemMode);
    }

    if (configReportItemNameExists != null) {
      Propagation.configWithPropagation(propagation, configReportItemNameExists.value(),
        BaseEntityContext::setReportItemNameExists, BaseEntityContext::getReportItemNameExists);
    }

    if (configInsertSelectAddItemMode != null) {
      Propagation.configWithPropagation(propagation, configInsertSelectAddItemMode.value(),
        BaseEntityContext::setInsertSelectAddItemMode, BaseEntityContext::getInsertSelectAddItemMode);
    }
  }

  public static void pushBaseEntity(Propagation propagation) {
    // NEW 压入新的
    if (propagation == null || propagation == Propagation.NEW || !BaseEntityContext.contextActive()) {
      BaseEntityContext.push();
    }
    // MERGE_* 压入带参数的
    else {
      BaseEntityContext.BaseEntityConfig baseEntityConfig = BaseEntityContext.peek();
      BaseEntityContext.push(new BaseEntityContext.BaseEntityConfig(baseEntityConfig));
    }
  }

  public static void configDynamicSQL(
    Propagation propagation, DataConvertorRegistry dataConvertorRegistry,
    DynamicConditions dynamicConditions, ConfigJoinUseSubQuery configJoinUseSubQuery,
    DynamicItems dynamicItems, ConfigDupThenNew configDupThenNew,
    ConfigDuplicateKeyUpdate configDuplicateKeyUpdate, ConfigUpdateItemMode configUpdateItemMode,
    ConfigInsertSelectAddItemMode configInsertSelectAddItemMode) {
    if (dynamicConditions != null) {
      DynamicCondition[] alDynamicCondition = dynamicConditions.value();
      for (DynamicCondition dynamicCondition : alDynamicCondition) {
        // 相同的condition只会注入一次，所以NEW、MERGE_NEW、MERGE_OLD三者的结果都是一样的
        DynamicSQLContext.addCondition(dynamicCondition.operation(), dynamicCondition.name(), dynamicCondition.condition());
      }
      if (configJoinUseSubQuery != null) {
        Propagation.configWithPropagation(propagation, configJoinUseSubQuery.value(),
          DynamicSQLContext::setJoinUseSubQuery, DynamicSQLContext::getJoinUseSubQuery);
      }
    }
    if (dynamicItems != null) {
      DynamicItem[] allDynamicItem = dynamicItems.value();
      // NEW 的时候 dupThenNew 的值为true和false是同样的效果
      // MERGE_NEW 的时候 itemValue 取新的
      // MERGE_OLD 的时候 itemValue 取旧的
      boolean dupThenNew;
      if (configDupThenNew != null) {
        dupThenNew = configDupThenNew.value();
      } else {
        dupThenNew = propagation == Propagation.NEW || propagation == Propagation.MERGE_NEW;
      }
      for (DynamicItem dynamicItem : allDynamicItem) {
        DynamicSQLContext.addItem(dynamicItem.operation(), dynamicItem.name(), dynamicItem.itemName(),
          dataConvertorRegistry.parse(dynamicItem.itemValue(), dynamicItem.clazz()), dupThenNew);
      }
      if (configDuplicateKeyUpdate != null) {
        Propagation.configWithPropagation(propagation, configDuplicateKeyUpdate.value(),
          DynamicSQLContext::setDuplicateKeyUpdate, DynamicSQLContext::getDuplicateKeyUpdate);
      }
      if (configUpdateItemMode != null) {
        Propagation.configWithPropagation(propagation, configUpdateItemMode.value(),
          DynamicSQLContext::setUpdateItemMode, DynamicSQLContext::getUpdateItemMode);
      }
      if (configInsertSelectAddItemMode != null) {
        Propagation.configWithPropagation(propagation, configInsertSelectAddItemMode.value(),
          DynamicSQLContext::setInsertSelectAddItemMode, DynamicSQLContext::getInsertSelectAddItemMode);
      }
    }
  }

  public static void pushDynamicSQL(Propagation propagation) {
    // NEW 或当前没有正在执行的context -> 压入新的
    if (propagation == Propagation.NEW || !DynamicSQLContext.contextActive()) {
      DynamicSQLContext.push();
    }
    // MERGE_* -> 压入带参数的
    else {
      Ternary<Map<String, List<Map.Entry<DynamicConditionOperation, String>>>,
        Map<String, List<Map.Entry<DynamicItemOperation, Item>>>,
        DynamicSQLContext.DynamicSQLConfig> peek = DynamicSQLContext.peek();
      Map<String, List<Map.Entry<DynamicConditionOperation, String>>> condition = new HashMap<>(peek.getF1());
      Map<String, List<Map.Entry<DynamicItemOperation, Item>>> item = new HashMap<>(peek.getF2());
      DynamicSQLContext.DynamicSQLConfig dynamicSQLConfig = new DynamicSQLContext.DynamicSQLConfig(peek.getF3());
      DynamicSQLContext.push(new Ternary<>(condition, item, dynamicSQLConfig));
    }
  }

  public static void configLockingReads(Propagation propagation, ConfigLock configLock) {
    if (configLock != null) {
      Propagation.configWithPropagation(propagation, configLock.value(),
        LockingReadsContext::setLock, LockingReadsContext::getLock);
    }
  }

  public static void pushLockingReads(Propagation propagation) {
    // NEW 压入新的
    if (propagation == Propagation.NEW || !LockingReadsContext.contextActive()) {
      LockingReadsContext.push();
    }
    // MERGE_* 压入带参数的
    else {
      LockingReadsContext.LockingReadsConfig lockingReadsConfig = LockingReadsContext.peek();
      LockingReadsContext.push(new LockingReadsContext.LockingReadsConfig(lockingReadsConfig));
    }
  }

  public static void configSQLCheck(
    Propagation propagation, UnCheckAllColumn unCheckAllColumn,
    UnCheckExactIdentifier unCheckExactIdentifier, UnCheckDmlCondition unCheckDmlCondition) {
    if (unCheckAllColumn != null) {
      Propagation.configWithPropagation(propagation, unCheckAllColumn.value(),
        SQLCheckContext::setCheckAllColumn, SQLCheckContext::getCheckAllColumn);
    }
    if (unCheckExactIdentifier != null) {
      Propagation.configWithPropagation(propagation, unCheckExactIdentifier.value(),
        SQLCheckContext::setCheckExactIdentifier, SQLCheckContext::getCheckExactIdentifier);
    }
    if (unCheckDmlCondition != null) {
      Propagation.configWithPropagation(propagation, unCheckDmlCondition.value(),
        SQLCheckContext::setCheckDmlCondition, SQLCheckContext::getCheckDmlCondition);
    }
  }

  public static void pushSQLCheck(Propagation propagation) {
    // NEW 压入新的
    if (propagation == Propagation.NEW || !SQLCheckContext.contextActive()) {
      SQLCheckContext.push();
    }
    // MERGE_* 压入带参数的
    else {
      SQLCheckContext.SQLCheckConfig sqlCheckConfig = SQLCheckContext.peek();
      SQLCheckContext.push(new SQLCheckContext.SQLCheckConfig(sqlCheckConfig));
    }
  }

  public static void configTombstone(
    Propagation propagation, DisableTombstone disableTombstone,
    ConfigJoinUseSubQuery configJoinUseSubQuery) {
    if (disableTombstone != null) {
      Propagation.configWithPropagation(propagation, disableTombstone.value(),
        TombstoneContext::setDisable, TombstoneContext::getDisable);
    }

    if (configJoinUseSubQuery != null) {
      Propagation.configWithPropagation(propagation, configJoinUseSubQuery.value(),
        TombstoneContext::setJoinUseSubQuery, TombstoneContext::getJoinUseSubQuery);
    }
  }

  public static void pushTombstone(Propagation propagation) {
    // NEW 压入新的
    if (propagation == Propagation.NEW || !TombstoneContext.contextActive()) {
      TombstoneContext.push();
    }
    // MERGE_* 压入带参数的
    else {
      TombstoneContext.TombstoneConfig tombstoneConfig = TombstoneContext.peek();
      TombstoneContext.push(new TombstoneContext.TombstoneConfig(tombstoneConfig));
    }
  }

}
