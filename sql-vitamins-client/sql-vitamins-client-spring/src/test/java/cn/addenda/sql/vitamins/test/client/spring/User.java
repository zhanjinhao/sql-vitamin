package cn.addenda.sql.vitamins.test.client.spring;

import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
  private Integer userId;
  private String userName;
  private Integer ifDel;

  private String aaa;
}
