package cn.addenda.sql.vitamin.rewriter.convertor.type;

import cn.addenda.sql.vitamin.rewriter.convertor.sqlexpr.SQLCharExprDataConvertor;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;

/**
 * @author addenda
 * @since 2022/9/10 16:57
 */
public class CharacterDataConvertor extends AbstractTypeDataConvertor<Character, SQLCharExpr> {

  public CharacterDataConvertor() {
    super(new SQLCharExprDataConvertor());
  }

  @Override
  public SQLCharExpr doParse(Object obj) {
    return new SQLCharExpr(String.valueOf(obj));
  }

}
