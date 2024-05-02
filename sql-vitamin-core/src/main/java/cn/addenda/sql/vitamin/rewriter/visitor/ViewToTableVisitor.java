package cn.addenda.sql.vitamin.rewriter.visitor;

import cn.addenda.sql.vitamin.rewriter.util.DruidSQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最终的结果有三种场景：<br/>
 * <pre>
 * - tableName -> tableName：select n from a
 * - alias -> tableName：select n from a t
 * - alias -> null： select n from (select n from a) t; select n from (select n from a union select n from b) t
 * </pre>
 *
 * @author addenda
 * @since 2023/5/03 20:09
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewToTableVisitor extends MySqlASTVisitorAdapter {

  public static final String VIEW_TO_TABLE_KEY = "view_to_table_key";

  private volatile static ViewToTableVisitor INSTANCE;

  public static ViewToTableVisitor getInstance() {
    if (INSTANCE != null) {
      return INSTANCE;
    }
    synchronized (ViewToTableVisitor.class) {
      if (INSTANCE == null) {
        INSTANCE = new ViewToTableVisitor();
      }
    }
    return INSTANCE;
  }

  /**
   * <pre>{@code
   *
   *             with tmp1 as
   *                     (select e1.deptno, round(avg(ifnull(e1.sal, 0)), 2) avg_sal
   *                             from emp e1
   *                             group by e1.deptno),
   *                     tmp2 as
   *                     (select e1.deptno, round(avg(ifnull(e1.sal, 0)), 2) avg_sal
   *                             from emp e1
   *                             where e1.sal > 1000
   *                             group by e1.deptno)
   *             select d.deptno, tmp1.avg_sal avg_sal1, tmp2.avg_sal avg_sal2
   *             from dept d
   *             left join tmp1
   *             on d.deptno = tmp1.deptno
   *             left join tmp2
   *             on d.deptno = tmp2.deptno
   *     }
   * </pre>
   */
  @Override
  public void endVisit(SQLWithSubqueryClause x) {
    // 不支持
  }

  /**
   * <pre>{@code
   *     select a from  log, unnest(cast(json_parse(number) as array(bigint))) as t(a)
   * }
   * </pre>
   */
  @Override
  public void endVisit(SQLUnnestTableSource x) {
    // 不支持
  }

  /**
   * <pre>{@code
   *     select c from (values (1), (2), (3)) as t(c)
   * }
   * </pre>
   */
  @Override
  public void endVisit(SQLValuesTableSource x) {
    // 不支持
  }

  @Override
  public void endVisit(SQLExprTableSource x) {
    String alias = x.getAlias();
    String tableName = x.getTableName();
    String view = alias == null ? tableName : alias;

    // 设置view_to_table
    Map<String, String> map = new HashMap<>();
    map.put(view, tableName);
    x.putAttribute(VIEW_TO_TABLE_KEY, map);
    logViewToTableMap(x, map);
  }

  @Override
  public void endVisit(SQLUnionQueryTableSource x) {
    String alias = x.getAlias();

    // 设置view_to_table
    Map<String, String> map = new HashMap<>();
    map.put(alias, null);
    x.putAttribute(VIEW_TO_TABLE_KEY, map);
    logViewToTableMap(x, map);
  }

  @Override
  public void endVisit(SQLSubqueryTableSource x) {
    String alias = x.getAlias();

    // 设置view_to_table
    Map<String, String> map = new HashMap<>();
    map.put(alias, null);
    x.putAttribute(VIEW_TO_TABLE_KEY, map);
    logViewToTableMap(x, map);
  }

  @Override
  public void endVisit(SQLJoinTableSource x) {
    SQLTableSource left = x.getLeft();
    SQLTableSource right = x.getRight();

    // 设置view_to_table
    Map<String, String> leftMap = getViewToTableMap(left);
    Map<String, String> rightMap = getViewToTableMap(right);
    Map<String, String> map = new HashMap<>();
    if (leftMap != null) {
      map.putAll(leftMap);
    }
    if (rightMap != null) {
      map.putAll(rightMap);
    }
    if (!map.isEmpty()) {
      x.putAttribute(VIEW_TO_TABLE_KEY, map);
      logViewToTableMap(x, map);
    }
  }

  @Override
  public void endVisit(SQLUnionQuery x) {
    List<SQLSelectQuery> relations = x.getRelations();
    Map<String, String> map = new HashMap<>();
    for (SQLSelectQuery sqlSelectQuery : relations) {
      map.putAll(getViewToTableMap(sqlSelectQuery));
    }
    x.putAttribute(VIEW_TO_TABLE_KEY, map);
    logViewToTableMap(x, map);
  }

  @Override
  public boolean visit(SQLSelectQueryBlock x) {
    return !ifVisited(x);
  }

  @Override
  public void endVisit(SQLSelectQueryBlock x) {
    Map<String, String> viewToTableMap = getViewToTableMap(x.getFrom());
    baseEndVisit(x, viewToTableMap);
  }

  @Override
  public boolean visit(MySqlUpdateStatement x) {
    return !ifVisited(x);
  }

  @Override
  public void endVisit(MySqlUpdateStatement x) {
    Map<String, String> viewToTableMap = getViewToTableMap(x.getTableSource());
    baseEndVisit(x, viewToTableMap);
  }

  @Override
  public boolean visit(MySqlDeleteStatement x) {
    return !ifVisited(x);
  }

  @Override
  public void endVisit(MySqlDeleteStatement x) {
    Map<String, String> viewToTableMap = getViewToTableMap(x.getTableSource());
    baseEndVisit(x, viewToTableMap);
  }

  @Override
  public boolean visit(MySqlInsertStatement x) {
    return !ifVisited(x);
  }

  @Override
  public void endVisit(MySqlInsertStatement x) {
    Map<String, String> viewToTableMap = getViewToTableMap(x.getTableSource());
    baseEndVisit(x, viewToTableMap);
  }

  private void baseEndVisit(SQLObject x, Map<String, String> viewToTableMap) {
    if (viewToTableMap != null) {
      Map<String, String> map = new HashMap<>(viewToTableMap);
      x.putAttribute(VIEW_TO_TABLE_KEY, map);
      logViewToTableMap(x, map);
    }
  }

//  private boolean ifVisited(SQLObject x) {
//    boolean b = ifCurNodeVisited(x);
//    if (b) {
//      log.error("SQLObject: [{}] 已经visit过，如果AST发生变更，请输出为SQL并重新生成AST后visit。", DruidSQLUtils.toLowerCaseSQL(x));
//    }
//    x.putAttribute("visited", true);
//    return b;
//  }
//
//  public static boolean ifCurNodeVisited(SQLObject x) {
//    Object visited = x.getAttribute("visited");
//    return visited != null;
//  }

  private boolean ifVisited(SQLObject x) {
    Object visited = x.getAttribute("visited");
    if (visited == null) {
      x.putAttribute("visited", true);
      return false;
    }
    // todo 修改为 throw exception，看还能不能过单元测试
    log.error("SQLObject: [{}] 已经visit过，如果AST发生变更，请输出为SQL并重新生成AST后visit。", DruidSQLUtils.toLowerCaseSQL(x));
    return true;
  }

  private void logViewToTableMap(SQLObject x, Map<String, String> map) {
    if (log.isDebugEnabled()) {
      log.debug("SQLObject: [{}]. viewToTableMap: [{}].", DruidSQLUtils.toLowerCaseSQL(x), map);
    }
  }

  public static Map<String, String> getViewToTableMap(SQLObject sqlObject) {
    return (Map<String, String>) sqlObject.getAttribute(VIEW_TO_TABLE_KEY);
  }

}
