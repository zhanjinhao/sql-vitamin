package cn.addenda.sql.vitamins.rewriter.visitor.item;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.ViewToTableVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author addenda
 * @since 2023/5/10 17:50
 */
@Slf4j
public class AddInsertItemVisitor extends AbstractAddItemVisitor<MySqlInsertStatement> {

  @Getter
  @Setter
  private List<Item> result;

  protected final DataConvertorRegistry dataConvertorRegistry;
  /**
   * <ul>
   *   <li>true: throw exception when itemName exists</li>
   *   <li>false: do not add item</li>
   * </ul>
   */
  protected final boolean reportItemNameExists;
  private final Item item;
  private final InsertAddSelectItemMode insertAddSelectItemMode;
  private final boolean duplicateKeyUpdate;
  private final UpdateItemMode updateItemMode;

  public AddInsertItemVisitor(String sql, Item item) {
    this(sql, null, null,
        new DefaultDataConvertorRegistry(), true,
        item, InsertAddSelectItemMode.VALUE, false, UpdateItemMode.NOT_NULL);
  }

  public AddInsertItemVisitor(
      String sql, List<String> included, List<String> notIncluded,
      DataConvertorRegistry dataConvertorRegistry, boolean reportItemNameExists,
      Item item, InsertAddSelectItemMode insertAddSelectItemMode,
      boolean duplicateKeyUpdate, UpdateItemMode updateItemMode) {
    super(sql, included, notIncluded);
    this.dataConvertorRegistry = dataConvertorRegistry;
    this.reportItemNameExists = reportItemNameExists;
    this.item = item;
    this.insertAddSelectItemMode = insertAddSelectItemMode;
    this.duplicateKeyUpdate = duplicateKeyUpdate;
    this.updateItemMode = updateItemMode;
  }

  public AddInsertItemVisitor(MySqlInsertStatement sql, Item item) {
    this(sql, null, null,
        new DefaultDataConvertorRegistry(), true,
        item, InsertAddSelectItemMode.VALUE, false, UpdateItemMode.NOT_NULL);
  }

  public AddInsertItemVisitor(
      MySqlInsertStatement sql, List<String> included, List<String> notIncluded,
      DataConvertorRegistry dataConvertorRegistry, boolean reportItemNameExists,
      Item item, InsertAddSelectItemMode insertAddSelectItemMode,
      boolean duplicateKeyUpdate, UpdateItemMode updateItemMode) {
    super(sql, included, notIncluded);
    this.dataConvertorRegistry = dataConvertorRegistry;
    this.reportItemNameExists = reportItemNameExists;
    this.item = item;
    this.insertAddSelectItemMode = insertAddSelectItemMode;
    this.duplicateKeyUpdate = duplicateKeyUpdate;
    this.updateItemMode = updateItemMode;
  }

  @Override
  public void endVisit(MySqlInsertStatement x) {
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getTableSource());
    if (viewToTableMap.size() != 1) {
      String msg = String.format("insert 语句增加item仅支持单表，SQL: [%s]。", DruidSQLUtils.toLowerCaseSQL(x));
      throw new SqlVitaminsException(msg);
    }
    String table = null;
    for (Entry<String, String> stringEntry : viewToTableMap.entrySet()) {
      table = stringEntry.getValue();
    }

    if (table == null) {
      String msg = String.format("找不到表名，SQL: [%s]。", DruidSQLUtils.toLowerCaseSQL(x));
      throw new SqlVitaminsException(msg);
    }

    if (!JdbcSQLUtils.include(table, included, notIncluded)) {
      return;
    }

    List<SQLExpr> columns = x.getColumns();

    if (checkItemNameExists(x, columns, item.getItemName(), reportItemNameExists)) {
      return;
    }

    if (log.isDebugEnabled()) {
      log.debug("SQLObject: [{}], 增加 itemName：[{}]。", DruidSQLUtils.toLowerCaseSQL(x), item.getItemName());
    }
    columns.add(SQLUtils.toSQLExpr(item.getItemName()));

    List<ValuesClause> valuesList = x.getValuesList();
    if (valuesList != null && !valuesList.isEmpty()) {
      for (ValuesClause valuesClause : valuesList) {
        if (log.isDebugEnabled()) {
          log.debug("SQLObject: [{}], 增加 itemValue：[{}]。", DruidSQLUtils.toLowerCaseSQL(x), item.getItemValue());
        }
        valuesClause.addValue(dataConvertorRegistry.parse(item.getItemValue()));
      }
      if (duplicateKeyUpdate) {
        List<SQLExpr> duplicateKeyUpdateList = x.getDuplicateKeyUpdate();
        if (UpdateItemMode.ALL == updateItemMode) {
          duplicateKeyUpdateList.add(newItemBinaryOpExpr(item.getItemName(), item.getItemValue()));
        } else if (UpdateItemMode.NOT_NULL == updateItemMode) {
          if (item.getItemValue() != null) {
            duplicateKeyUpdateList.add(newItemBinaryOpExpr(item.getItemName(), item.getItemValue()));
          }
        } else if (UpdateItemMode.NOT_EMPTY == updateItemMode) {
          if (item.getItemValue() instanceof CharSequence
              && !JdbcSQLUtils.isEmpty((CharSequence) item.getItemValue())) {
            duplicateKeyUpdateList.add(newItemBinaryOpExpr(item.getItemName(), item.getItemValue()));
          }
        }
      }
    }

    SQLSelect sqlSelect = x.getQuery();
    if (sqlSelect != null) {
      // sqlSelect的返回值可能已经存在 itemName了，再增加一个也无问题
      sqlSelectQueryAddSelectItem(sqlSelect.getQuery());
    }
  }

  private void sqlSelectQueryAddSelectItem(SQLSelectQuery query) {
    // insert into A(name) select name from B
    if (query instanceof MySqlSelectQueryBlock) {
      MySqlSelectQueryBlock mySqlSelectQueryBlock = (MySqlSelectQueryBlock) query;
      if (insertAddSelectItemMode == InsertAddSelectItemMode.DB) {
        SQLSelectStatement sqlSelectStatement = wrapSQLSelectQuery(mySqlSelectQueryBlock);
        AddSelectItemVisitor visitor = new AddSelectItemVisitor(
            sqlSelectStatement, included, notIncluded, false, null, item.getItemName());
        visitor.visit();
        String resultItemName = visitor.getResult();
        if (resultItemName == null) {
          String msg = String.format("无法从SQL中推断出来需要增加的itemName，SQL：[%s]，item：[%s]。",
              DruidSQLUtils.toLowerCaseSQL(query), item);
          throw new SqlVitaminsException(msg);
        }
      } else if (insertAddSelectItemMode == InsertAddSelectItemMode.VALUE) {
        addItemFromValue(mySqlSelectQueryBlock);
      }
    }
    // insert into t_user_0(username) select username from t_user_1 union select username from t_user_2
    // insert into t_user_0(username) (select username from t_user_1 union select username from t_user_2)
    else if (query instanceof SQLUnionQuery) {
      SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) query;
      List<SQLSelectQuery> relations = sqlUnionQuery.getRelations();
      for (SQLSelectQuery relation : relations) {
        sqlSelectQueryAddSelectItem(relation);
      }
    }
  }

  private void addItemFromValue(MySqlSelectQueryBlock mySqlSelectQueryBlock) {
    SQLSelectItem sqlSelectItem = new SQLSelectItem();
    sqlSelectItem.setAlias(item.getItemName());
    sqlSelectItem.setExpr(dataConvertorRegistry.parse(item.getItemValue()));
    mySqlSelectQueryBlock.addSelectItem(sqlSelectItem);
    if (log.isDebugEnabled()) {
      log.debug("SQLObject: [{}], 增加 item：[{}]。", DruidSQLUtils.toLowerCaseSQL(mySqlSelectQueryBlock), item);
    }
  }

  private SQLSelectStatement wrapSQLSelectQuery(SQLSelectQuery query) {
    SQLSelectStatement sqlSelectStatement = new SQLSelectStatement();
    SQLSelect sqlSelect = new SQLSelect();
    sqlSelectStatement.setSelect(sqlSelect);
    sqlSelect.setParent(sqlSelectStatement);
    sqlSelect.setQuery(query);
    query.setParent(sqlSelect);
    return sqlSelectStatement;
  }

  protected SQLExpr newItemBinaryOpExpr(String itemName, Object itemValue) {
    SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr();
    sqlBinaryOpExpr.setLeft(new SQLIdentifierExpr(itemName));
    sqlBinaryOpExpr.setOperator(SQLBinaryOperator.Equality);
    sqlBinaryOpExpr.setRight(dataConvertorRegistry.parse(itemValue));
    return sqlBinaryOpExpr;
  }

  @Override
  public String toString() {
    return "InsertAddItemVisitor{" +
        "item=" + item +
        ", reportItemNameExists=" + reportItemNameExists +
        ", insertAddSelectItemMode=" + insertAddSelectItemMode +
        ", duplicateKeyUpdate=" + duplicateKeyUpdate +
        ", updateItemMode=" + updateItemMode +
        "} " + super.toString();
  }
}
