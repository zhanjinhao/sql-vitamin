package cn.addenda.sql.vitamin.rewriter.baseentity;

import cn.addenda.sql.vitamin.rewriter.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/8/16 20:40
 */
@JsonIgnoreProperties({
    BaseEntity.N_CREATOR, BaseEntity.N_CREATOR_NAME, BaseEntity.N_CREATE_TIME, BaseEntity.N_MODIFIER,
    BaseEntity.N_MODIFIER_NAME, BaseEntity.N_MODIFY_TIME, BaseEntity.N_REMARK})
public abstract class BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String N_CREATOR = "creator";
  public static final String N_CREATOR_NAME = "creatorName";
  public static final String N_CREATE_TIME = "createTime";
  public static final String N_MODIFIER = "modifier";
  public static final String N_MODIFIER_NAME = "modifierName";
  public static final String N_MODIFY_TIME = "modifyTime";
  public static final String N_REMARK = "remark";

  public static List<String> getAllColumnNameList() {
    return getAllFieldNameList().stream().map(BaseEntity::camelCaseToSnakeCase).collect(Collectors.toList());
  }

  public static List<String> getAllFieldNameList() {
    List<String> fieldNameList = new ArrayList<>();
    Class<BaseEntity> baseEntityClass = BaseEntity.class;
    Field[] declaredFields = baseEntityClass.getDeclaredFields();
    for (Field field : declaredFields) {
      if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      fieldNameList.add(field.getName());
    }
    return fieldNameList;
  }

  public static List<String> getUpdateColumnNameList() {
    return getUpdateFieldNameList().stream().map(BaseEntity::camelCaseToSnakeCase).collect(Collectors.toList());
  }

  public static List<String> getUpdateFieldNameList() {
    List<String> fieldNameList = new ArrayList<>();
    Class<BaseEntity> baseEntityClass = BaseEntity.class;
    Field[] declaredFields = baseEntityClass.getDeclaredFields();
    for (Field field : declaredFields) {
      if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
          || !field.isAnnotationPresent(UpdateField.class)) {
        continue;
      }
      fieldNameList.add(field.getName());
    }
    return fieldNameList;
  }

  private static String camelCaseToSnakeCase(String camelCase) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < camelCase.length(); i++) {
      char ch = camelCase.charAt(i);
      if (Character.isUpperCase(ch)) {
        builder.append("_");
      }
      builder.append(Character.toLowerCase(ch));
    }
    return builder.toString();
  }

  private String creator;
  private String creatorName;

  @JsonSerialize(using = LocalDateTimeStrSerializer.class)
  @JsonDeserialize(using = LocalDateTimeStrDeSerializer.class)
  private LocalDateTime createTime;

  @UpdateField
  private String modifier;
  @UpdateField
  private String modifierName;

  @JsonSerialize(using = LocalDateTimeStrSerializer.class)
  @JsonDeserialize(using = LocalDateTimeStrDeSerializer.class)
  @UpdateField
  private LocalDateTime modifyTime;

  @UpdateField
  private String remark;

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public String getModifier() {
    return modifier;
  }

  public void setModifier(String modifier) {
    this.modifier = modifier;
  }

  public String getModifierName() {
    return modifierName;
  }

  public void setModifierName(String modifierName) {
    this.modifierName = modifierName;
  }

  public LocalDateTime getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(LocalDateTime modifyTime) {
    this.modifyTime = modifyTime;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Override
  public String toString() {
    return "BaseEntity{" +
        "creator='" + creator + '\'' +
        ", creatorName='" + creatorName + '\'' +
        ", createTime=" + createTime +
        ", modifier='" + modifier + '\'' +
        ", modifierName='" + modifierName + '\'' +
        ", modifyTime=" + modifyTime +
        ", remark='" + remark + '\'' +
        '}';
  }

  private static class LocalDateTimeStrDeSerializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode jsonNode = jp.getCodec().readTree(jp);
      final String s = jsonNode.asText();
      return DateUtils.parseLdt(s, DateUtils.FULL_FORMATTER);
    }
  }

  private static class LocalDateTimeStrSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      jgen.writeString(DateUtils.format(localDateTime, DateUtils.FULL_FORMATTER));
    }
  }

}
