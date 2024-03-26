package cn.addenda.sql.vitamins.client.mybatis.helper;

import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;

/**
 * @author addenda
 * @since 2023/6/11 12:42
 */
public class PageHelperMsIdExtractHelper extends AbstractMsIdExtractHelper {

  private static class InnerPageHelperMsIdExtractHelper {
    private static final PageHelperMsIdExtractHelper instance = new PageHelperMsIdExtractHelper();
  }

  public static PageHelperMsIdExtractHelper getInstance() {
    return InnerPageHelperMsIdExtractHelper.instance;
  }

  public PageHelperMsIdExtractHelper() {
    super(ArrayUtils.asHashSet("_COUNT"));
  }

}
