package cn.addenda.sql.vitamins.rewriter.visitor.item;

public enum UpdateItemMode {
  ALL,
  NOT_NULL,
  /**
   * EMPTY的定义：""
   */
  NOT_EMPTY,
}