package cn.addenda.sql.vitamins.test.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntity;
import cn.addenda.sql.vitamins.rewriter.baseentity.DefaultBaseEntitySource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author addenda
 * @since 2023/5/3 17:13
 */
public class DefaultBaseEntitySourceTest {

  @Test
  public void test() {
    DefaultBaseEntitySource defaultBaseEntityContext = new DefaultBaseEntitySource();
    Assert.assertEquals("addenda", defaultBaseEntityContext.get(BaseEntity.N_CREATOR));
    Assert.assertEquals("addenda", defaultBaseEntityContext.get(BaseEntity.N_CREATOR_NAME));
    Assert.assertEquals("now(3)", defaultBaseEntityContext.get(BaseEntity.N_CREATE_TIME));
    Assert.assertEquals("addenda", defaultBaseEntityContext.get(BaseEntity.N_MODIFIER));
    Assert.assertEquals("addenda", defaultBaseEntityContext.get(BaseEntity.N_MODIFIER_NAME));
    Assert.assertEquals("now(3)", defaultBaseEntityContext.get(BaseEntity.N_MODIFY_TIME));
    DefaultBaseEntitySource.setRemark("defaultBaseEntitySource1");
    Assert.assertEquals("defaultBaseEntitySource1", defaultBaseEntityContext.get(BaseEntity.N_REMARK));
    Assert.assertEquals("defaultBaseEntitySource1", defaultBaseEntityContext.get(BaseEntity.N_REMARK));

    DefaultBaseEntitySource.execute("defaultBaseEntitySource2", () -> {
      Assert.assertEquals("defaultBaseEntitySource2", defaultBaseEntityContext.get(BaseEntity.N_REMARK));
    });
    Assert.assertEquals(null, defaultBaseEntityContext.get(BaseEntity.N_REMARK));
  }

}
