package cn.addenda.sql.vitamins.rewriter.visitor.item;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;
import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.ViewToTableVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2023/5/1 13:28
 */
@Slf4j
public class AddSelectItemVisitor extends AbstractAddItemVisitor<SQLSelectStatement> {

  @Getter
  @Setter
  private String result;

  private static final String ITEM_KEY = "AddSelectItemKey";
  /**
   * <ul>
   *   <li>true: throw exception when itemName exists</li>
   *   <li>false: continue add itemName</li>
   * </ul>
   */
  private final boolean reportItemNameExists;

  private String masterView;

  private final String itemName;

  public AddSelectItemVisitor(
      String sql, List<String> included, List<String> notIncluded,
      boolean reportItemNameExists, String masterView, String itemName) {
    super(sql, included, notIncluded);
    this.reportItemNameExists = reportItemNameExists;
    this.masterView = masterView;
    this.itemName = itemName;
  }

  public AddSelectItemVisitor(String sql, String masterView, String itemName) {
    this(sql, null, null, true, masterView, itemName);
  }

  public AddSelectItemVisitor(
      SQLSelectStatement sqlSelectStatement, List<String> included, List<String> notIncluded,
      boolean reportItemNameExists, String masterView, String itemName) {
    super(sqlSelectStatement, included, notIncluded);
    this.reportItemNameExists = reportItemNameExists;
    this.masterView = masterView;
    this.itemName = itemName;
  }

  public AddSelectItemVisitor(
      SQLSelectStatement sqlSelectStatement, String masterView, String itemName) {
    this(sqlSelectStatement, null, null, true, masterView, itemName);
  }

  private int depth = 0;

  @Override
  public void endVisit(SQLSelectGroupByClause x) {
    List<SQLExpr> items = x.getItems();

    // items 里面存在的基础列才能被注入到返回值
    List<SQLExpr> injectedList = new ArrayList<>();
    for (SQLExpr sqlExpr : items) {
      if (JdbcSQLUtils.include(JdbcSQLUtils.extractColumnName(sqlExpr.toString()), ArrayUtils.asArrayList(itemName), null)) {
        injectedList.add(sqlExpr);
      }
    }

    if (injectedList.isEmpty()) {
      // 没有列可以注入时，需要移出之前遗留的列
      x.getAttributes().remove(ITEM_KEY);
      return;
    }

    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getParent());
    List<MySelectItem> mySelectItemList = new ArrayList<>();
    for (SQLExpr sqlExpr : injectedList) {
      String owner = JdbcSQLUtils.extractColumnOwner(sqlExpr.toString());
      if (owner == null) {
        List<String> declaredTableList = new ArrayList<>();
        viewToTableMap.forEach((view, tableName) -> {
          if (tableName != null && JdbcSQLUtils.include(tableName, included, notIncluded)) {
            declaredTableList.add(tableName);
          }
        });

        if (declaredTableList.size() == 1) {
          String view = declaredTableList.get(0);
          mySelectItemList.add(new MySelectItem(SQLUtils.toSQLExpr(view + "." + sqlExpr), view + "_" + sqlExpr));
        } else if (declaredTableList.size() > 1) {
          String ambiguousInfo = String.format("SQLObject: [%s], Ambiguous identifier: [%s], declaredTableList: [%s].",
              DruidSQLUtils.toLowerCaseSQL(x), DruidSQLUtils.toLowerCaseSQL(sqlExpr), declaredTableList);
          throw new SqlVitaminsException(ambiguousInfo);
        } else {
          // no-op
        }
      } else {
        String tableName = viewToTableMap.get(owner);
        if (tableName != null && JdbcSQLUtils.include(tableName, included, notIncluded)) {
          mySelectItemList.add(new MySelectItem(sqlExpr, sqlExpr.toString().replace(".", "_")));
        }
      }
    }
    putItemList(x, mySelectItemList);
  }

  @Override
  public void endVisit(SQLExprTableSource x) {
    String alias = x.getAlias();
    String tableName = x.getTableName();
    String view = alias == null ? tableName : alias;
    if (!JdbcSQLUtils.include(tableName, included, notIncluded)) {
      return;
    }
    List<MySelectItem> mySelectItemList = new ArrayList<>();
    mySelectItemList.add(
        new MySelectItem(SQLUtils.toSQLExpr(view + "." + itemName), view + "_" + itemName));
    putItemList(x, mySelectItemList);

    addMasterTableName(x, tableName);
    addMasterAlias(x, alias);
  }

  @Override
  public void endVisit(SQLSubqueryTableSource x) {
    SQLSelect select = x.getSelect();
    String alias = x.getAlias();
    List<MySelectItem> mySelectItemList = getItemList(select);
    if (mySelectItemList != null) {
      List<MySelectItem> xMySelectItemList = new ArrayList<>();
      for (MySelectItem item : mySelectItemList) {
        xMySelectItemList.add(new MySelectItem(
            SQLUtils.toSQLExpr(alias + "." + item.getAlias()), alias + "_" + item.getAlias()));
      }
      putItemList(x, xMySelectItemList);
    }

    addMasterAlias(x, alias);
  }

  @Override
  public void endVisit(SQLJoinTableSource x) {
    inherit(x);

    SQLTableSource left = x.getLeft();
    SQLTableSource right = x.getRight();

    List<MySelectItem> mySelectItemList = new ArrayList<>();
    List<MySelectItem> leftMySelectItemList = getItemList(left);
    if (leftMySelectItemList != null) {
      mySelectItemList.addAll(leftMySelectItemList);
    }
    List<MySelectItem> rightMySelectItemList = getItemList(right);
    if (rightMySelectItemList != null) {
      mySelectItemList.addAll(rightMySelectItemList);
    }
    putItemList(x, mySelectItemList);
  }

  @Override
  public void endVisit(SQLUnionQueryTableSource x) {
    SQLUnionQuery union = x.getUnion();
    String alias = x.getAlias();
    List<MySelectItem> mySelectItemList = getItemList(union);
    List<MySelectItem> xResult = new ArrayList<>();
    for (MySelectItem item : mySelectItemList) {
      xResult.add(new MySelectItem(
          SQLUtils.toSQLExpr(alias + "." + item.getAlias()), alias + "_" + item.getAlias()));
    }
    putItemList(x, xResult);
  }

  @Override
  public void endVisit(SQLUnionQuery x) {
    List<SQLSelectQuery> relations = x.getRelations();
    List<MySelectItem> list = getItemList(relations.get(0));
    boolean flag = true;
    for (int i = 1; i < relations.size(); i++) {
      SQLSelectQuery relation = relations.get(i);
      List<MySelectItem> relationMySelectItemList = getItemList(relation);
      if (relationMySelectItemList == null) {
        flag = false;
        break;
      }
      if (list.size() != relationMySelectItemList.size()) {
        flag = false;
        break;
      }
      for (int j = 0; j < list.size(); j++) {
        MySelectItem o1 = list.get(j);
        MySelectItem o2 = relationMySelectItemList.get(j);
        if (!o1.equals(o2)) {
          flag = false;
          break;
        }
      }
      if (!flag) {
        break;
      }
    }
    if (flag) {
      putItemList(x, new ArrayList<>(list));
    } else {
      clear(x);
    }
  }

  private void clear(SQLSelectQuery sqlSelectQuery) {
    if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
      SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
      List<SQLSelectItem> selectList = sqlSelectQueryBlock.getSelectList();
      selectList.removeIf(MySelectItem.class::isInstance);
    } else if (sqlSelectQuery instanceof SQLUnionQuery) {
      SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
      List<SQLSelectQuery> relations = sqlUnionQuery.getRelations();
      for (SQLSelectQuery relation : relations) {
        clear(relation);
      }
    }
  }

  @Override
  public void endVisit(SQLSelectQueryBlock x) {
    SQLTableSource from = x.getFrom();
    SQLSelectGroupByClause groupBy = x.getGroupBy();
    List<SQLSelectItem> selectList = x.getSelectList();
    // todo *和A.* 场景处理 不支持返回值有*的情况
    List<SQLExpr> selectExprSet = selectList.stream().map(SQLSelectItem::getExpr).collect(Collectors.toList());
    checkItemNameExists(x, selectExprSet, itemName, reportItemNameExists);

    List<MySelectItem> mySelectItemList;
    if (groupBy != null) {
      mySelectItemList = getItemList(groupBy);
    } else {
      mySelectItemList = getItemList(from);
    }
    if (mySelectItemList != null) {
      putItemList(x, new ArrayList<>(mySelectItemList));
      for (MySelectItem mySelectItem : mySelectItemList) {
        SQLExpr expr = mySelectItem.getExpr();
        if (!selectExprSet.contains(expr)) {
          if (log.isDebugEnabled()) {
            log.debug("SQLObject: [{}], 注入列：[{}].", DruidSQLUtils.toLowerCaseSQL(x), mySelectItem);
          }
          x.addSelectItem(mySelectItem);
        }
      }
    }

    if (depth == 1) {
      boolean flag = false;
      if (masterView == null) {
        flag = true;
        if (from instanceof SQLExprTableSource) {
          SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) from;
          String tableName = sqlExprTableSource.getTableName();
          String alias = sqlExprTableSource.getAlias();
          masterView = alias == null ? tableName : alias;
        } else if (from instanceof SQLSubqueryTableSource) {
          SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) from;
          masterView = sqlSubqueryTableSource.getAlias();
        } else if (from instanceof SQLUnionQueryTableSource) {
          SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource) from;
          masterView = sqlUnionQueryTableSource.getAlias();
        } else if (from instanceof SQLJoinTableSource) {
          // 只有left join或者right join在这里才能取到值
          String masterAlias = getMasterAlias(from);
          if (masterAlias != null) {
            masterView = masterAlias;
          } else {
            String masterTableName = getMasterTableName(from);
            if (masterTableName != null) {
              masterView = masterTableName;
            } else {
              if (mySelectItemList != null) {
                // 如果注入的列名的owner都一样，将owner设置为masterView
                Set<String> prefixSet = new HashSet<>();
                for (MySelectItem mySelectItem : mySelectItemList) {
                  String prefix = JdbcSQLUtils.extractColumnOwner(mySelectItem.getExpr().toString().toLowerCase());
                  if (prefix != null) {
                    prefixSet.add(prefix);
                  }
                }
                if (prefixSet.size() == 1) {
                  masterView = prefixSet.iterator().next();
                }
              }
            }
          }
        }
      }

      if (masterView == null) {
        List<SQLSelectItem> injected = selectList.stream()
            .filter(MySelectItem.class::isInstance)
            .filter(f -> f.getAlias().endsWith(itemName)).collect(Collectors.toList());
        if (injected.size() == 1) {
          setResult(itemName);
        }
      } else {
        // 获取到masterView下注入的字段
        List<SQLSelectItem> injected = selectList.stream()
            .filter(MySelectItem.class::isInstance)
            .filter(f -> f.getAlias().toLowerCase().startsWith(masterView.toLowerCase() + "_"))
            .filter(f -> f.getAlias().endsWith(itemName)).collect(Collectors.toList());

        // 只有当masterView注入的字段个数为1时才进行masterView改写
        if (injected.size() == 1) {
          injected.get(0).setAlias(itemName);
          setResult(itemName);
        }
      }

      if (flag) {
        masterView = null;
      }
    }
  }

  @Override
  public boolean visit(SQLSelect x) {
    depth++;
    return true;
  }

  @Override
  public void endVisit(SQLSelect select) {
    SQLSelectQuery query = select.getQuery();
    List<MySelectItem> itemList = getItemList(query);
    if (itemList != null) {
      putItemList(select, new ArrayList<>(itemList));
    }
    depth--;
  }

  private static final String MASTER_TABLE_NAME_KEY = "MASTER_TABLE_NAME_KEY";
  private static final String MASTER_ALIAS_KEY = "MASTER_ALIAS_KEY";

  protected String getMasterTableName(SQLObject sqlObject) {
    return (String) sqlObject.getAttribute(MASTER_TABLE_NAME_KEY);
  }

  protected String getMasterAlias(SQLObject sqlObject) {
    return (String) sqlObject.getAttribute(MASTER_ALIAS_KEY);
  }

  protected void addMasterTableName(SQLObject sqlObject, String tableName) {
    if (tableName != null) {
      sqlObject.putAttribute(MASTER_TABLE_NAME_KEY, tableName);
    }
  }

  protected void addMasterAlias(SQLObject sqlObject, String alias) {
    if (alias != null) {
      sqlObject.putAttribute(MASTER_ALIAS_KEY, alias);
    }
  }

  private void inherit(SQLObject x, SQLTableSource tableSource) {
    String whereRightTableName = getMasterTableName(tableSource);
    String whereRightAlias = getMasterAlias(tableSource);
    if (whereRightTableName != null) {
      addMasterTableName(x, whereRightTableName);
    }
    if (whereRightAlias != null) {
      addMasterAlias(x, whereRightAlias);
    }
  }

  private void inherit(SQLJoinTableSource x) {
    SQLTableSource left = x.getLeft();
    SQLTableSource right = x.getRight();

    SQLJoinTableSource.JoinType joinType = x.getJoinType();
    if (SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN == joinType) {
      inherit(x, left);
    } else if (SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN == joinType) {
      inherit(x, right);
    }
  }

  @Override
  public void endVisit(SQLValuesQuery x) {
  }

  @Override
  public boolean visit(SQLSelectItem x) {
    return false;
  }

  @Override
  public boolean visit(SQLBinaryOpExpr x) {
    return false;
  }

  @Override
  public boolean visit(SQLExistsExpr x) {
    return false;
  }

  @Override
  public boolean visit(SQLInSubQueryExpr x) {
    return false;
  }

  @Override
  public boolean visit(SQLContainsExpr x) {
    return false;
  }

  @Override
  public boolean visit(SQLBetweenExpr x) {
    return false;
  }

  private List<MySelectItem> getItemList(SQLObject sqlObject) {
    return (List<MySelectItem>) sqlObject.getAttribute(ITEM_KEY);
  }

  private void putItemList(SQLObject sqlObject, List<MySelectItem> mySelectItemList) {
    if (mySelectItemList != null && !mySelectItemList.isEmpty()) {
      // 对相同的SQLSelectStatement进行多次visit时，attribute里会遗留上次注入的列，所以需要清除非当前itemName后缀的列
      mySelectItemList.removeIf(a -> !a.getAlias().toLowerCase().endsWith(itemName.toLowerCase()));
      sqlObject.putAttribute(ITEM_KEY, mySelectItemList);
    }
  }

  public static class MySelectItem extends SQLSelectItem {

    public MySelectItem(SQLExpr expr, String alias) {
      super(expr, alias);
    }

  }

  @Override
  public String toString() {
    return "AddSelectItemVisitor{" +
        "reportItemNameExists=" + reportItemNameExists +
        ", masterView='" + masterView + '\'' +
        ", itemName='" + itemName + '\'' +
        "} " + super.toString();
  }
}
