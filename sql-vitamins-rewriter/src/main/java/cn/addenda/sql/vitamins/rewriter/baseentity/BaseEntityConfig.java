package cn.addenda.sql.vitamins.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntityConfig {
  private Boolean disable;
  private String masterView;
  private Boolean duplicateKeyUpdate;
  private InsertAddSelectItemMode insertAddSelectItemMode;
  private UpdateItemMode updateItemMode;

  public BaseEntityConfig(BaseEntityConfig baseEntityConfig) {
    this.disable = baseEntityConfig.disable;
    this.masterView = baseEntityConfig.masterView;
    this.duplicateKeyUpdate = baseEntityConfig.duplicateKeyUpdate;
    this.insertAddSelectItemMode = baseEntityConfig.insertAddSelectItemMode;
    this.updateItemMode = baseEntityConfig.updateItemMode;
  }

  public static BaseEntityConfig of(
      Boolean disable, String masterView, Boolean duplicateKeyUpdate,
      InsertAddSelectItemMode insertAddSelectItemMode, UpdateItemMode updateItemMode) {
    return new BaseEntityConfig(disable, masterView, duplicateKeyUpdate, insertAddSelectItemMode, updateItemMode);
  }

}
