package cn.addenda.sql.vitamins.rewriter.lockingread;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LockingReadConfig {

  private String lock;

  public LockingReadConfig(LockingReadConfig lockingReadConfig) {
    this.lock = lockingReadConfig.lock;
  }

  public static LockingReadConfig of(String lock) {
    return new LockingReadConfig(lock);
  }
}
