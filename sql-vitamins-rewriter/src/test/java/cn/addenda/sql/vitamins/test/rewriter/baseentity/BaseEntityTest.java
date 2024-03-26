package cn.addenda.sql.vitamins.test.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntity;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author addenda
 * @since 2023/5/3 17:09
 */
public class BaseEntityTest {

  @Test
  public void test() {
    Assert.assertEquals("[modifier, modifierName, modifyTime, remark]", BaseEntity.getUpdateFieldNameList().toString());
    Assert.assertEquals("[modifier, modifier_name, modify_time, remark]", BaseEntity.getUpdateColumnNameList().toString());
    Assert.assertEquals("[creator, creatorName, createTime, modifier, modifierName, modifyTime, remark]", BaseEntity.getAllFieldNameList().toString());
    Assert.assertEquals("[creator, creator_name, create_time, modifier, modifier_name, modify_time, remark]", BaseEntity.getAllColumnNameList().toString());
  }

}
