package cn.addenda.sql.vitamin.test.client.spring.dynamic.suffix;

import cn.addenda.sql.vitamin.rewriter.baseentity.DefaultBaseEntitySource;
import cn.addenda.sql.vitamin.rewriter.dynamic.item.DynamicItemConfig;
import cn.addenda.sql.vitamin.rewriter.dynamic.item.DynamicItemConfigBatch;
import cn.addenda.sql.vitamin.rewriter.dynamic.item.DynamicItemOperation;
import cn.addenda.sql.vitamin.rewriter.dynamic.item.DynamicItemUtils;
import cn.addenda.sql.vitamin.rewriter.dynamic.suffix.DynamicSuffixUtils;
import cn.addenda.sql.vitamin.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamin.test.client.spring.User;
import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2024/4/5 10:04
 */
public class SpringDynamicSuffixTest {

  static AnnotationConfigApplicationContext context;

  static DataSource dataSource;

  @BeforeClass
  public static void before() {
    context = new AnnotationConfigApplicationContext();
    context.register(DynamicSuffixTestConfiguration.class);
    context.refresh();
    dataSource = context.getBean(DataSource.class);
  }

  @Test
  public void test() {
    createUserTable();
    insertUser(1, "a", "aaa", "b");
    insertUser(2, "q", "aaa", "b");

    User user1 = getAllUsers();
    Assert.assertTrue(user1.getUserId() != 0);
    Assert.assertNotNull(user1.getUserId());
    Assert.assertEquals(2, user1.getUserId().intValue());
    Assert.assertNotNull(user1.getUserName());
    Assert.assertEquals("q", user1.getUserName());
    Assert.assertNotNull(user1.getCreator());
    Assert.assertNotNull(user1.getCreatorName());
    Assert.assertNotNull(user1.getCreateTime());
    Assert.assertNotNull(user1.getModifier());
    Assert.assertNotNull(user1.getModifierName());
    Assert.assertNotNull(user1.getModifyTime());
    Assert.assertNotNull(user1.getRemark());
    Assert.assertNotNull(user1.getIfDel());
    Assert.assertEquals("b", user1.getAaa());
    Assert.assertEquals(0, user1.getIfDel().intValue());
    Assert.assertEquals(user1.getCreateTime(), user1.getModifyTime());
  }

  @AfterClass
  public static void after() {
    context.close();
  }

  private String create_ddl =
      "create table users\n" +
          "(\n" +
          "    id           bigint      not null\n" +
          "        primary key,\n" +
          "    name     varchar(20) not null,\n" +
          "    if_del        tinyint     null,\n" +
          "    creator       varchar(10) null,\n" +
          "    creator_name  varchar(10) null,\n" +
          "    create_time   datetime(3) null,\n" +
          "    modifier      varchar(10) null,\n" +
          "    modifier_name varchar(10) null,\n" +
          "    modify_time   datetime(3) null,\n" +
          "    remark        varchar(50) null,\n" +
          "    aaa        varchar(50) null\n" +
          ")";

  @SneakyThrows
  public void createUserTable() {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(create_ddl)) {
      preparedStatement.executeUpdate();
    }
  }

  @SneakyThrows
  public void insertUser(int id, String name, String itemName, String itemValue) {
    DynamicItemUtils.dynamicItem(DynamicItemConfigBatch.of(
        ArrayUtils.asArrayList(DynamicItemConfig.of(
            DynamicItemOperation.INSERT_ADD_ITEM, "users", itemName, itemValue))
    ), () -> {
      DefaultBaseEntitySource.execute("aaa", new Runnable() {
        @Override
        @SneakyThrows
        public void run() {
          try (Connection connection = dataSource.getConnection();
               PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)")) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
          }
        }
      });
    });
  }

  @SneakyThrows
  public User getAllUsers() {
    return DynamicSuffixUtils.suffix("order by id desc limit 1", new Supplier<User>() {
      @Override
      @SneakyThrows
      public User get() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name, if_del, aaa FROM users");
             ResultSet resultSet = preparedStatement.executeQuery()) {

          User user = null;
          while (resultSet.next()) {
            user = new User();
            user.setUserId(resultSet.getInt("id"));
            user.setUserName(resultSet.getString("name"));
            user.setCreator(resultSet.getString("creator"));
            user.setCreatorName(resultSet.getString("creator_name"));
            user.setCreateTime(resultSet.getTimestamp("create_time").toLocalDateTime());
            user.setModifier(resultSet.getString("modifier"));
            user.setModifierName(resultSet.getString("modifier_name"));
            user.setModifyTime(resultSet.getTimestamp("modify_time").toLocalDateTime());
            user.setRemark(resultSet.getString("remark"));
            user.setIfDel(resultSet.getInt("if_del"));
            user.setAaa(resultSet.getString("aaa"));
          }
          return user;
        }
      }
    });
  }

}
