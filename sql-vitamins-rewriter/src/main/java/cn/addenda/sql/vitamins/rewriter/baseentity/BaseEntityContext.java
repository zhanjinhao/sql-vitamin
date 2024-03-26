package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.*;

import java.util.Stack;

/**
 * @author addenda
 * @since 2023/5/3 18:13
 */
public class BaseEntityContext {

  private BaseEntityContext() {
  }

  private static final ThreadLocal<Stack<BaseEntityConfig>> BASE_ENTITY_CONFIG_TL = ThreadLocal.withInitial(() -> null);

  public static void setDisable(boolean disable) {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    baseEntityConfig.setDisable(disable);
  }

  public static Boolean getDisable() {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    return baseEntityConfig.getDisable();
  }

  public static void setMasterView(String masterView) {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    baseEntityConfig.setMasterView(masterView);
  }

  public static String getMasterView() {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    return baseEntityConfig.getMasterView();
  }

  public static void setReportItemNameExists(boolean reportItemNameExists) {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    baseEntityConfig.setReportItemNameExists(reportItemNameExists);
  }

  public static Boolean getReportItemNameExists() {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    return baseEntityConfig.getReportItemNameExists();
  }

  public static void setDuplicateKeyUpdate(boolean duplicateKeyUpdate) {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    baseEntityConfig.setDuplicateKeyUpdate(duplicateKeyUpdate);
  }

  public static Boolean getDuplicateKeyUpdate() {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    return baseEntityConfig.getDuplicateKeyUpdate();
  }

  public static void setInsertSelectAddItemMode(InsertAddSelectItemMode insertAddSelectItemMode) {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    baseEntityConfig.setInsertAddSelectItemMode(insertAddSelectItemMode);
  }

  public static InsertAddSelectItemMode getInsertSelectAddItemMode() {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    return baseEntityConfig.getInsertAddSelectItemMode();
  }

  public static void setUpdateItemMode(UpdateItemMode updateItemMode) {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    baseEntityConfig.setUpdateItemMode(updateItemMode);
  }

  public static UpdateItemMode getUpdateItemMode() {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigs.peek();
    return baseEntityConfig.getUpdateItemMode();
  }

  public static void push() {
    push(new BaseEntityConfig());
  }

  public static void pop() {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    baseEntityConfigs.pop();
    if (baseEntityConfigs.isEmpty()) {
      BASE_ENTITY_CONFIG_TL.remove();
    }
  }

  public static BaseEntityConfig peek() {
    return BASE_ENTITY_CONFIG_TL.get().peek();
  }

  public static boolean contextActive() {
    return BASE_ENTITY_CONFIG_TL.get() != null;
  }

  public static void push(BaseEntityConfig baseEntityConfig) {
    Stack<BaseEntityConfig> baseEntityConfigs = BASE_ENTITY_CONFIG_TL.get();
    if (baseEntityConfigs == null) {
      baseEntityConfigs = new Stack<>();
      BASE_ENTITY_CONFIG_TL.set(baseEntityConfigs);
    }
    baseEntityConfigs.push(baseEntityConfig);
  }

  @Setter
  @Getter
  @ToString
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BaseEntityConfig {
    private Boolean disable;
    private String masterView;
    private Boolean reportItemNameExists;
    private Boolean duplicateKeyUpdate;
    private InsertAddSelectItemMode insertAddSelectItemMode;
    private UpdateItemMode updateItemMode;

    public BaseEntityConfig(BaseEntityConfig baseEntityConfig) {
      this.disable = baseEntityConfig.disable;
      this.masterView = baseEntityConfig.masterView;
      this.reportItemNameExists = baseEntityConfig.reportItemNameExists;
      this.duplicateKeyUpdate = baseEntityConfig.duplicateKeyUpdate;
      this.insertAddSelectItemMode = baseEntityConfig.insertAddSelectItemMode;
      this.updateItemMode = baseEntityConfig.updateItemMode;
    }
  }

}
