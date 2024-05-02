package cn.addenda.sql.vitamin.test.core;

import cn.addenda.sql.vitamin.rewriter.convertor.AbstractDataConvertor;
import cn.addenda.sql.vitamin.rewriter.convertor.AbstractDataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.convertor.DataConvertor;
import cn.addenda.sql.vitamin.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.convertor.type.CharSequenceDataConvertor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author addenda
 * @since 2023/6/9 22:25
 */
public class RegistryTest {

  @Test
  public void test1() {

    Date date = new Date();
    Date sqldate = new java.sql.Date(System.currentTimeMillis());
    LocalDateTime localDateTime = LocalDateTime.now();
    LocalDate localDate = LocalDate.now();
    LocalTime localTime = LocalTime.now();
    ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC+9"));
    OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.of("UTC+9"));

    DefaultDataConvertorRegistry registry = new DefaultDataConvertorRegistry(ZoneId.of("UTC+7"));
    Assert.assertEquals("1", registry.format(1));
    Assert.assertEquals("'str'", registry.format("str"));
    Assert.assertEquals("'sb'", registry.format(new StringBuilder("sb")));
    Assert.assertEquals("'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "'", registry.format(date));
    Assert.assertEquals("'" + new SimpleDateFormat("yyyy-MM-dd").format(sqldate) + "'", registry.format(sqldate));
    Assert.assertEquals("'" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime) + "'", registry.format(localDateTime));
    Assert.assertEquals("'" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zonedDateTime.plusHours(-2)) + "'", registry.format(zonedDateTime));
    Assert.assertEquals("'" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(offsetDateTime.plusHours(-2)) + "'", registry.format(offsetDateTime));
    Assert.assertEquals("'" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate) + "'", registry.format(localDate));
    Assert.assertEquals("'" + DateTimeFormatter.ofPattern("HH:mm:ss").format(localTime) + "'", registry.format(localTime));


    System.out.println("\n--------------------- fastDataConvertorMap ------------------\n");
    registry.getFastDataConvertorMap().forEach(new BiConsumer<Class<?>, DataConvertor<?, ?>>() {
      @Override
      public void accept(Class<?> aClass, DataConvertor<?, ?> dataConvertor) {
        System.out.println("class: " + aClass.getName() + ", convertor: " + dataConvertor + "。");
      }
    });
  }

  @Test
  public void test2() {
    AbstractDataConvertorRegistry registry = new AbstractDataConvertorRegistry() {
      @Override
      protected void init() {
        addDataConvertor(new CharSequenceDataConvertor(this));
        addDataConvertor(new AbstractDataConvertor<String, SQLExpr>() {
          @Override
          public Class<String> getType() {
            return String.class;
          }

          @Override
          public String format(Object obj) {
            return obj.toString();
          }

          @Override
          public SQLExpr doParse(Object obj) {
            String text = String.valueOf(obj);
            return new SQLCharExpr("tb" + text + "tf");
          }

          @Override
          public SQLExpr doParse(String str) {
            return null;
          }

          @Override
          public boolean strMatch(String str) {
            return false;
          }
        });
      }
    };

    // aaa是String类型，距离AbstractDataConvertor<String, SQLExpr>更近，所以解析成tbaaatf
    Assert.assertEquals("'tbaaatf'", registry.parse("aaa").toString());
  }

  @Test
  public void test3() {
    DefaultDataConvertorRegistry registry = new DefaultDataConvertorRegistry(ZoneId.of("Asia/Bangkok"));
    Assert.assertEquals("DATETIME '2020-12-12 12:12:12'", registry.parse("DATETIME 2020-12-12 12:12:12", LocalDateTime.class).toString());
    Assert.assertEquals("DATETIME '2020-12-12 11:12:12'", registry.parse("2020-12-12 12:12:12", ZonedDateTime.class).toString());
  }

  @Test
  public void test4() {
    String sql = "update t set a = 'aaaa'";
    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, "mysql");
    System.out.println(sqlStatements.get(0));
  }

  @Test
  public void test5() {
    CharSequenceDataConvertor charSequenceDataConvertor = new CharSequenceDataConvertor(new DefaultDataConvertorRegistry());
    Assert.assertEquals("'123\\\\'''", charSequenceDataConvertor.format("123\\'"));
  }

}
