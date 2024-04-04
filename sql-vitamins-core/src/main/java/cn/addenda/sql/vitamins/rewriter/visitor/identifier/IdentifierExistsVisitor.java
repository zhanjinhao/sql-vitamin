package cn.addenda.sql.vitamins.rewriter.visitor.identifier;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;
import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.rewriter.util.DruidSQLUtils;
import cn.addenda.sql.vitamins.rewriter.util.JdbcSQLUtils;
import cn.addenda.sql.vitamins.rewriter.visitor.ViewToTableVisitor;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 假如有表A、B、C，其中B和C是存在列name的，要检测SQL里面有没有B和C的name列。<br/>
 * 如果表集合为空，检测全部的表。
 *
 * @author addenda
 * @since 2023/5/3 20:55
 */
@Slf4j
public class IdentifierExistsVisitor extends AbstractIdentifierVisitor {

  private final List<String> included;

  private final List<String> notIncluded;

  @Getter
  protected boolean exists = false;

  public IdentifierExistsVisitor(
      String sql, String identifier,
      List<String> included, List<String> notIncluded) {
    super(sql, identifier);
    this.included = included;
    this.notIncluded = notIncluded;
  }

  public IdentifierExistsVisitor(
      SQLStatement sql, String identifier,
      List<String> included, List<String> notIncluded) {
    super(sql, identifier);
    this.included = included;
    this.notIncluded = notIncluded;
  }

  public IdentifierExistsVisitor(String sql, String identifier) {
    this(sql, identifier, null, null);
  }

  public IdentifierExistsVisitor(SQLStatement sql, String identifier) {
    this(sql, identifier, null, null);
  }

  @Override
  public SQLStatement visit() {
    sqlStatement.accept(ViewToTableVisitor.getInstance());
    sqlStatement.accept(this);
    return sqlStatement;
  }

  @Override
  public void endVisit(SQLSelectQueryBlock x) {
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getFrom());
    List<String> identifierList = identifierListStack.peek();

    if (exists) {
      // 如果已经存在，就直接返回了
    } else {
      handle(identifierList, viewToTableMap, x);
    }
    logExists(x, viewToTableMap, identifierList);
    super.endVisit(x);
  }

  private void handle(List<String> identifierList, Map<String, String> viewToTableMap, SQLObject x) {
    for (String _identifier : identifierList) {
      String owner = JdbcSQLUtils.extractColumnOwner(_identifier);
      if (owner == null) {
        List<String> declaredTableList = new ArrayList<>();
        viewToTableMap.forEach((view, table) -> {
          if (table != null && JdbcSQLUtils.include(table, included, notIncluded)) {
            declaredTableList.add(table);
          }
        });

        // 如果只有一个表存在字段，则identifier存在
        if (declaredTableList.size() == 1) {
          exists = true;
          break;
        }
        // 如果多个表存在字段，则抛出异常
        else if (declaredTableList.size() > 1) {
          String ambiguousInfo = String.format("SQLObject: [%s], Ambiguous identifier: [%s], declaredTableList: [%s].",
              DruidSQLUtils.toLowerCaseSQL(x), identifier, declaredTableList);
          throw new SqlVitaminsException(ambiguousInfo);
        }
        // 如果没有表存在字段，则表示不是availableList里的表
        else {
          // no-op
        }
      } else {
        String tableName = viewToTableMap.get(owner);
        if (tableName != null && JdbcSQLUtils.include(tableName, included, notIncluded)) {
          exists = true;
          break;
        }
      }
    }
  }

  @Override
  public void endVisit(MySqlInsertStatement x) {
    List<String> identifierList = identifierListStack.peek();
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getTableSource());
    handle(identifierList, viewToTableMap, x);
    logExists(x, viewToTableMap, identifierList);
    super.endVisit(x);
  }

  @Override
  public void endVisit(MySqlUpdateStatement x) {
    List<String> identifierList = identifierListStack.peek();
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getTableSource());
    handle(identifierList, viewToTableMap, x);
    logExists(x, viewToTableMap, identifierList);
    super.endVisit(x);
  }

  @Override
  public void endVisit(MySqlDeleteStatement x) {
    List<String> identifierList = identifierListStack.peek();
    Map<String, String> viewToTableMap = ViewToTableVisitor.getViewToTableMap(x.getTableSource());
    handle(identifierList, viewToTableMap, x);
    logExists(x, viewToTableMap, identifierList);
    super.endVisit(x);
  }

  private void logExists(SQLObject x, Map<String, String> viewToTableMap, List<String> identifierList) {
    if (log.isDebugEnabled()) {
      log.debug("SQLObject: [{}], viewToTableMap: [{}], identifierList: [{}], exists: [{}].",
          DruidSQLUtils.toLowerCaseSQL(x), viewToTableMap, identifierList, exists);
    }
  }

  @Override
  public String toString() {
    return "IdentifierExistsVisitor{" +
        "included=" + included +
        ", notIncluded=" + notIncluded +
        ", exists=" + exists +
        "} " + super.toString();
  }
}

