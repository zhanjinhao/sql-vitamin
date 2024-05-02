package cn.addenda.sql.vitamin.test.client.spring;

import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
  private Integer userId;
  private String userName;
  private Integer ifDel;
  private LocalDateTime deleteTime;

  private String aaa;
}
