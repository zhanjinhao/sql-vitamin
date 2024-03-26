package cn.addenda.sql.vitamins.rewriter.convertor;

import cn.addenda.sql.vitamins.rewriter.SqlVitaminsException;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import lombok.Getter;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author addenda
 * @since 2022/9/10 20:10
 */
public abstract class AbstractDataConvertorRegistry implements DataConvertorRegistry {

  protected final ZoneId zoneId;

  @Getter
  protected final Map<Class<?>, DataConvertor<?, ?>> dataConvertorMap = new HashMap<>();

  @Getter
  private final Map<Class<?>, DataConvertor<?, ?>> fastDataConvertorMap = new ConcurrentHashMap<>();

  protected AbstractDataConvertorRegistry(ZoneId zoneId) {
    this.zoneId = zoneId;
    init();
  }

  protected AbstractDataConvertorRegistry() {
    this.zoneId = ZoneId.systemDefault();
    init();
  }

  private final DataConvertor<Object, SQLExpr> null_ = new DataConvertor<Object, SQLExpr>() {
    @Override
    public Class<Object> getType() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String format(Object obj) {
      throw new UnsupportedOperationException();
    }

    @Override
    public SQLExpr parse(Object obj) {
      throw new UnsupportedOperationException();
    }

    @Override
    public SQLExpr parse(String str) {
      throw new UnsupportedOperationException();
    }
  };

  @Override
  public String format(Object obj) {
    if (obj == null) {
      return "null";
    }
    DataConvertor<?, ?> dataConvertor = determineDataConvertor(obj.getClass(), true);
    return dataConvertor.format(obj);
  }

  @Override
  public SQLExpr parse(Object obj) {
    if (obj == null) {
      return new SQLNullExpr();
    }
    DataConvertor<?, ?> dataConvertor = determineDataConvertor(obj.getClass(), true);
    return dataConvertor.parse(obj);
  }

  @Override
  public SQLExpr parse(String str, Class<?> clazz) {
    if (str == null) {
      return new SQLNullExpr();
    }
    DataConvertor<?, ?> dataConvertor = determineDataConvertor(clazz, true);
    return dataConvertor.parse(str);
  }

  @Override
  public boolean typeAvailable(Class<?> clazz) {
    return determineDataConvertor(clazz, false) != null_;
  }

  protected void addDataConvertor(DataConvertor<?, ?> dataConvertor) {
    Class<?> type = dataConvertor.getType();
    if (dataConvertorMap.containsKey(type)) {
      throw new SqlVitaminsException(type.getSimpleName() + ", dataConvertor has exists! ");
    }
    dataConvertorMap.put(type, dataConvertor);
  }

  protected abstract void init();

  private DataConvertor<?, ?> determineDataConvertor(Class<?> clazz, boolean report) {
    DataConvertor<?, ?> dataConvertor = fastDataConvertorMap.computeIfAbsent(clazz, this::compute);
    if (dataConvertor == null_ && report) {
      String msg = String.format("无法处理的类型：[%s]。", clazz.getName());
      throw new SqlVitaminsException(msg);
    }
    return dataConvertor;
  }

  private DataConvertor<?, ?> compute(Class<?> clazz) {
    Set<DataConvertor<?, ?>> treeSet = new TreeSet<>((o1, o2) -> {
      Class<?> type1 = o1.getType();
      Class<?> type2 = o2.getType();
      if (type1.isAssignableFrom(type2)) {
        return 1;
      } else if (type2.isAssignableFrom(type2)) {
        return -1;
      }
      return 0;
    });

    dataConvertorMap.forEach((type, dataConvertor) -> {
      if (type.isAssignableFrom(clazz)) {
        treeSet.add(dataConvertor);
      }
    });

    DataConvertor<?, ?> result = null_;
    for (DataConvertor<?, ?> dataConvertor : treeSet) {
      result = dataConvertor;
      break;
    }
    return result;
  }

  @Override
  public String toString() {
    return "AbstractDataConvertorRegistry{" +
      "zoneId=" + zoneId +
      ", dataConvertorMap=" + dataConvertorMap +
      ", fastDataConvertorMap=" + fastDataConvertorMap +
      '}';
  }

}
