package cn.addenda.sql.vitamins.rewriter.visitor.item;

import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.ViewToTableVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2023/5/10 17:50
 */
@Slf4j
public class AddUpdateItemVisitor extends AbstractAddItemVisitor<MySqlUpdateStatement> {

  @Getter
  @Setter
  private List<Item> result;

  private final DataConvertorRegistry dataConvertorRegistry;
  /**
   * <ul>
   *   <li>true: throw exception when itemName exists</li>
   *   <li>false: do not add item</li>
   * </ul>
   */
  private final boolean reportItemNameExists;
  private final Item item;
  private final UpdateItemMode updateItemMode;

  public AddUpdateItemVisitor(String sql, Item item) {
    this(sql, null, null,
        new DefaultDataConvertorRegistry(), false,
        item, UpdateItemMode.NOT_NULL);
  }

  public AddUpdateItemVisitor(
      String sql, List<String> included, List<String> excluded,
      DataConvertorRegistry dataConvertorRegistry, boolean reportItemNameExists,
      Item item, UpdateItemMode updateItemMode) {
    super(sql, included, excluded);
    this.dataConvertorRegistry = dataConvertorRegistry;
    this.reportItemNameExists = reportItemNameExists;
    this.item = item;
    this.updateItemMode = updateItemMode;
  }

  public AddUpdateItemVisitor(MySqlUpdateStatement sql, Item item) {
    this(sql, null, null,
        new DefaultDataConvertorRegistry(), false,
        item, UpdateItemMode.NOT_NULL);
  }

  public AddUpdateItemVisitor(
      MySqlUpdateStatement sql, List<String> included, List<String> excluded,
      DataConvertorRegistry dataConvertorRegistry, boolean reportItemNameExists,
      Item item, UpdateItemMode updateItemMode) {
    super(sql, included, excluded);
    this.dataConvertorRegistry = dataConvertorRegistry;
    this.reportItemNameExists = reportItemNameExists;
    this.item = item;
    this.updateItemMode = updateItemMode;
  }

  @Override
  public void endVisit(MySqlUpdateStatement x) {
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getTableSource());

    List<SQLUpdateSetItem> sqlUpdateSetItemList = x.getItems();

    List<SQLExpr> columns = sqlUpdateSetItemList.stream().map(SQLUpdateSetItem::getColumn).collect(Collectors.toList());
    if (checkItemNameExists(x, columns, item.getItemName(), reportItemNameExists)) {
      return;
    }

    boolean prefix = viewToTableMap.size() != 1;
    List<Item> itemList = new ArrayList<>();
    viewToTableMap.forEach((view, table) -> {
      if (table != null && JdbcSQLUtils.include(table, included, excluded)) {
        if (UpdateItemMode.ALL == updateItemMode) {
          itemList.add(addItem(x, view, prefix, sqlUpdateSetItemList));
        } else if (UpdateItemMode.NOT_NULL == updateItemMode) {
          if (item.getItemValue() != null) {
            itemList.add(addItem(x, view, prefix, sqlUpdateSetItemList));
          }
        } else if (UpdateItemMode.NOT_EMPTY == updateItemMode) {
          if (item.getItemValue() instanceof CharSequence && !JdbcSQLUtils.isEmpty((CharSequence) item.getItemValue())) {
            itemList.add(addItem(x, view, prefix, sqlUpdateSetItemList));
          }
        }
      }
    });
    if (!itemList.isEmpty()) {
      setResult(itemList);
    }
  }

  private Item addItem(SQLObject x, String view, boolean prefix, List<SQLUpdateSetItem> updateItemList) {
    SQLExpr sqlExpr;
    if (prefix) {
      sqlExpr = SQLUtils.toSQLExpr(view + "." + item.getItemName());
    } else {
      sqlExpr = SQLUtils.toSQLExpr(item.getItemName());
    }
    SQLUpdateSetItem sqlUpdateSetItem = new SQLUpdateSetItem();
    sqlUpdateSetItem.setColumn(sqlExpr);
    sqlUpdateSetItem.setValue(dataConvertorRegistry.parse(item.getItemValue()));
    if (log.isDebugEnabled()) {
      log.debug("SQLObject: [{}], 增加 item：[{}]。", DruidSQLUtils.toLowerCaseSQL(x), sqlUpdateSetItem);
    }

    updateItemList.add(sqlUpdateSetItem);
    return new Item(view + "." + item.getItemName(), item.getItemValue());
  }

  @Override
  public String toString() {
    return "AddUpdateItemVisitor{" +
        "dataConvertorRegistry=" + dataConvertorRegistry +
        ", reportItemNameExists=" + reportItemNameExists +
        ", item=" + item +
        ", updateItemMode=" + updateItemMode +
        "} " + super.toString();
  }
}
