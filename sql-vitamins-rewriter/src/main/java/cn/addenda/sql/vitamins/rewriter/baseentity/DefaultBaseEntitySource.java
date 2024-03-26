package cn.addenda.sql.vitamins.rewriter.baseentity;

/**
 * @author addenda
 * @since 2023/5/2 19:37
 */
public class DefaultBaseEntitySource extends ThreadLocalRemarkBaseEntitySource {

  @Override
  public String getCreator() {
    return "addenda";
  }

  @Override
  public String getCreatorName() {
    return "addenda";
  }

  @Override
  public Object getCreateTime() {
    return "now(3)";
  }

  @Override
  public String getModifier() {
    return "addenda";
  }

  @Override
  public String getModifierName() {
    return "addenda";
  }

  @Override
  public Object getModifyTime() {
    return "now(3)";
  }

}
