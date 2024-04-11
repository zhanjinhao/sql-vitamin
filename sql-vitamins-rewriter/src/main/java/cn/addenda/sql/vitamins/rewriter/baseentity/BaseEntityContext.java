package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.*;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author addenda
 * @since 2023/5/3 18:13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseEntityContext {

  private static final ThreadLocal<Deque<BaseEntityConfig>> BASE_ENTITY_TL = ThreadLocal.withInitial(() -> null);

  public static void setDisable(boolean disable) {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    baseEntityConfig.setDisable(disable);
  }

  public static Boolean getDisable() {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    return baseEntityConfig.getDisable();
  }

  public static void setSelectDisable(boolean selectDisable) {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    baseEntityConfig.setSelectDisable(selectDisable);
  }

  public static Boolean getSelectDisable() {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    return baseEntityConfig.getSelectDisable();
  }

  public static void setCompatibleMode(boolean disable) {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    baseEntityConfig.setCompatibleMode(disable);
  }

  public static Boolean getCompatibleMode() {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    return baseEntityConfig.getCompatibleMode();
  }

  public static void setMasterView(String masterView) {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    baseEntityConfig.setMasterView(masterView);
  }

  public static String getMasterView() {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    return baseEntityConfig.getMasterView();
  }

  public static void setDuplicateKeyUpdate(boolean duplicateKeyUpdate) {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    baseEntityConfig.setDuplicateKeyUpdate(duplicateKeyUpdate);
  }

  public static Boolean getDuplicateKeyUpdate() {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    return baseEntityConfig.getDuplicateKeyUpdate();
  }

  public static void setInsertSelectAddItemMode(InsertAddSelectItemMode insertAddSelectItemMode) {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    baseEntityConfig.setInsertAddSelectItemMode(insertAddSelectItemMode);
  }

  public static InsertAddSelectItemMode getInsertSelectAddItemMode() {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    return baseEntityConfig.getInsertAddSelectItemMode();
  }

  public static void setUpdateItemMode(UpdateItemMode updateItemMode) {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    baseEntityConfig.setUpdateItemMode(updateItemMode);
  }

  public static UpdateItemMode getUpdateItemMode() {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig baseEntityConfig = baseEntityConfigDeque.peek();
    return baseEntityConfig.getUpdateItemMode();
  }

  public static BaseEntityConfig pop() {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    BaseEntityConfig pop = baseEntityConfigDeque.pop();
    if (baseEntityConfigDeque.isEmpty()) {
      BASE_ENTITY_TL.remove();
    }
    return pop;
  }

  public static void remove() {
    BaseEntityContext.remove();
  }

  public static BaseEntityConfig peek() {
    return BASE_ENTITY_TL.get().peek();
  }

  public static boolean contextActive() {
    return BASE_ENTITY_TL.get() != null;
  }

  public static void push(BaseEntityConfig baseEntityConfig) {
    Deque<BaseEntityConfig> baseEntityConfigDeque = BASE_ENTITY_TL.get();
    if (baseEntityConfigDeque == null) {
      baseEntityConfigDeque = new ArrayDeque<>();
      BASE_ENTITY_TL.set(baseEntityConfigDeque);
    }
    baseEntityConfigDeque.push(baseEntityConfig);
  }

}
