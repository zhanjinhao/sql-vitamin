package cn.addenda.sql.vitamin.client.mybatis.helper;

/**
 * @author addenda
 * @since 2023/6/11 12:31
 */
public class DefaultMsIdExtractHelper extends AbstractMsIdExtractHelper {

  private static class InnerDefaultMsIdExtractHelper {
    private static final DefaultMsIdExtractHelper instance = new DefaultMsIdExtractHelper();
  }

  public static DefaultMsIdExtractHelper getInstance() {
    return InnerDefaultMsIdExtractHelper.instance;
  }

  public DefaultMsIdExtractHelper() {
    super(null);
  }
}
