package cn.addenda.sql.vitamins.test.client.spring.dynamic.item;

import cn.addenda.sql.vitamins.rewriter.baseentity.DefaultBaseEntitySource;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionConfig;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionConfigBatch;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionOperation;
import cn.addenda.sql.vitamins.rewriter.dynamic.condition.DynamicConditionUtils;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemConfig;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemConfigBatch;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemOperation;
import cn.addenda.sql.vitamins.rewriter.dynamic.item.DynamicItemUtils;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneConfig;
import cn.addenda.sql.vitamins.rewriter.tombstone.TombstoneUtils;
import cn.addenda.sql.vitamins.rewriter.util.ArrayUtils;
import cn.addenda.sql.vitamins.test.client.spring.User;
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
public class SpringDynamicItemTest {

  static AnnotationConfigApplicationContext context;

  static DataSource dataSource;

  @BeforeClass
  public static void before() {
    context = new AnnotationConfigApplicationContext();
    context.register(DynamicItemTestConfiguration.class);
    context.refresh();
    dataSource = context.getBean(DataSource.class);
  }

  @Test
  public void test() {
    createUserTable();
    insertUser(1, "a", "aaa", "b");

    User user1 = getAllUsers();
    Assert.assertTrue(user1.getUserId() != 0);
    Assert.assertNotNull(user1.getUserName());
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
    Assert.assertEquals("a", user1.getUserName());
    Assert.assertEquals(user1.getCreateTime(), user1.getModifyTime());

    updateUser(1, "b", "aaa", "c");
    User user2 = getAllUsers();
    Assert.assertEquals("b", user2.getUserName());
    Assert.assertEquals("c", user2.getAaa());
    Assert.assertNotEquals(user2.getCreateTime(), user2.getModifyTime());

    updateUserFail(1, "c");

    User user21 = getAllUsers();
    Assert.assertEquals("b", user21.getUserName());

    delete(1);
    User user3 = getAllUsers();
    Assert.assertNull(user3);

    User user4 = forceQuery();
    Assert.assertTrue(user4.getUserId() != 0);
    Assert.assertNotNull(user4.getUserName());
    Assert.assertNotNull(user4.getCreator());
    Assert.assertNotNull(user4.getCreatorName());
    Assert.assertNotNull(user4.getCreateTime());
    Assert.assertNotNull(user4.getModifier());
    Assert.assertNotNull(user4.getModifierName());
    Assert.assertNotNull(user4.getModifyTime());
    Assert.assertNotNull(user4.getRemark());
    Assert.assertNotNull(user4.getIfDel());
    Assert.assertEquals(1, user4.getIfDel().intValue());
  }

  @SneakyThrows
  private void updateUserFail(int id, String name) {
    DynamicConditionUtils.dynamicCondition(
        DynamicConditionConfigBatch.of(
            ArrayUtils.asArrayList(DynamicConditionConfig.of(
                DynamicConditionOperation.TABLE_ADD_JOIN_CONDITION, "users", "if_del = 1"))),
        new Runnable() {
          @Override
          @SneakyThrows
          public void run() {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("update users set name = ? where id = ?")) {
              preparedStatement.setString(1, name);
              preparedStatement.setInt(2, id);
              preparedStatement.executeUpdate();
            }
          }
        });
  }

  @SneakyThrows
  private void delete(int id) {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("delete from users where id = ?")) {
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
    }
  }

  @SneakyThrows
  private User forceQuery() {
    return TombstoneUtils.tombstone(TombstoneConfig.of(true, null, null, null),
        new Supplier<User>() {
          @Override
          @SneakyThrows
          public User get() {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name, if_del FROM users");
                 ResultSet resultSet = preparedStatement.executeQuery()) {

              User user = new User();
              while (resultSet.next()) {
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
              }
              return user;
            }
          }
        });
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
  public void updateUser(int id, String name, String itemName, String itemValue) {
    Thread.sleep(100);
    DynamicItemUtils.dynamicItem(DynamicItemConfigBatch.of(
        ArrayUtils.asArrayList(DynamicItemConfig.of(
            DynamicItemOperation.UPDATE_ADD_ITEM, "users", itemName, itemValue))
    ), new Runnable() {
      @Override
      @SneakyThrows
      public void run() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update users set name = ? where id = ?")) {
          preparedStatement.setString(1, name);
          preparedStatement.setInt(2, id);
          preparedStatement.executeUpdate();
        }
      }
    });
  }

  @SneakyThrows
  public User getAllUsers() {
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

}
