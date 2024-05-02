package cn.addenda.sql.vitamin.rewriter.baseentity;

import cn.addenda.sql.vitamin.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamin.rewriter.visitor.item.UpdateItemMode;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntityConfig {
  private Boolean disable;
  private Boolean selectDisable;
  private Boolean compatibleMode;
  private String masterView;
  private Boolean duplicateKeyUpdate;
  private InsertAddSelectItemMode insertAddSelectItemMode;
  private UpdateItemMode updateItemMode;

  public BaseEntityConfig(BaseEntityConfig baseEntityConfig) {
    this.disable = baseEntityConfig.disable;
    this.selectDisable = baseEntityConfig.selectDisable;
    this.compatibleMode = baseEntityConfig.compatibleMode;
    this.masterView = baseEntityConfig.masterView;
    this.duplicateKeyUpdate = baseEntityConfig.duplicateKeyUpdate;
    this.insertAddSelectItemMode = baseEntityConfig.insertAddSelectItemMode;
    this.updateItemMode = baseEntityConfig.updateItemMode;
  }

  public static BaseEntityConfig of(
      Boolean disable, Boolean selectDisable, Boolean compatibleMode, String masterView, Boolean duplicateKeyUpdate,
      InsertAddSelectItemMode insertAddSelectItemMode, UpdateItemMode updateItemMode) {
    return new BaseEntityConfig(disable, selectDisable, compatibleMode, masterView,
        duplicateKeyUpdate, insertAddSelectItemMode, updateItemMode);
  }

}
