package cn.addenda.sql.vitamins.test.core;

import cn.addenda.sql.vitamins.rewriter.convertor.AbstractDataConvertor;
import cn.addenda.sql.vitamins.rewriter.convertor.AbstractDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.DataConvertor;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.convertor.type.CharSequenceDataConvertor;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    ass(registry.format(1), "1");
    ass(registry.format("str"), "'str'");
    ass(registry.format(new StringBuilder("sb")), "'sb'");
    ass(registry.format(date), "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "'");
    ass(registry.format(sqldate), "'" + new SimpleDateFormat("yyyy-MM-dd").format(sqldate) + "'");
    ass(registry.format(localDateTime), "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime) + "'");
    ass(registry.format(zonedDateTime), "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zonedDateTime.plusHours(-2)) + "'");
    ass(registry.format(offsetDateTime), "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(offsetDateTime.plusHours(-2)) + "'");
    ass(registry.format(localDate), "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate) + "'");
    ass(registry.format(localTime), "'" + DateTimeFormatter.ofPattern("HH:mm:ss").format(localTime) + "'");


    System.out.println("\n--------------------- fastDataConvertorMap ------------------\n");
    registry.getFastDataConvertorMap().forEach(new BiConsumer<Class<?>, DataConvertor<?, ?>>() {
      @Override
      public void accept(Class<?> aClass, DataConvertor<?, ?> dataConvertor) {
        System.out.println("class: " + aClass.getName() + ", convertor: " + dataConvertor + "。");
      }
    });
  }

  private void ass(String s1, String s2) {
    if (s1.equals(s2)) {
      System.out.println("actual: " + s1 + ", expect: " + s2);
    } else {
      System.err.println("actual: " + s1 + ", expect: " + s2);
      throw new RuntimeException();
    }
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

    ass(registry.format("aaa"), "'tbaaatf'");
  }

  @Test
  public void test3() {
    DefaultDataConvertorRegistry registry = new DefaultDataConvertorRegistry(ZoneId.of("UTC+7"));
    ass(registry.parse("DATETIME 2020-12-12 12:12:12", LocalDateTime.class).toString(), "DATETIME '2020-12-12 12:12:12'");
    // todo
//        ass(registry.parse("2020-12-12 12:12:12", ZonedDateTime.class).toString(), "DATETIME '2020-12-12 12:12:12'");

  }

}
