package cn.addenda.sql.vitamin.rewriter.visitor.item;

public enum UpdateItemMode {
  ALL,
  NOT_NULL,
  /**
   * EMPTY的定义：""
   */
  NOT_EMPTY,
}