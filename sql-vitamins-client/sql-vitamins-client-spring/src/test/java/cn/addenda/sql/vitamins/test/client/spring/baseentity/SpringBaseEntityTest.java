package cn.addenda.sql.vitamins.test.client.spring.baseentity;

import cn.addenda.sql.vitamins.rewriter.baseentity.DefaultBaseEntitySource;
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

/**
 * @author addenda
 * @since 2024/4/5 10:04
 */
public class SpringBaseEntityTest {

  static AnnotationConfigApplicationContext context;

  static DataSource dataSource;

  @BeforeClass
  public static void before() {
    context = new AnnotationConfigApplicationContext();
    context.register(BaseEntityTestConfiguration.class);
    context.refresh();
    dataSource = context.getBean(DataSource.class);
  }

  @Test
  public void test() {
    createUserTable();
    insertUser(1, "a");

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
    Assert.assertEquals(user1.getCreateTime(), user1.getModifyTime());

    updateUser(1, "b");
    User user2 = getAllUsers();
    Assert.assertNotEquals(user2.getCreateTime(), user2.getModifyTime());
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
          "    remark        varchar(50) null\n" +
          ")";

  @SneakyThrows
  public void createUserTable() {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(create_ddl)) {
      preparedStatement.executeUpdate();
    }
  }

  @SneakyThrows
  public void insertUser(int id, String name) {
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
  }

  @SneakyThrows
  public void updateUser(int id, String name) {
    Thread.sleep(100);
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("update users set name = ? where id = ?")) {
      preparedStatement.setString(1, name);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
    }
  }

  @SneakyThrows
  public User getAllUsers() {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name FROM users");
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
      }
      return user;
    }
  }

}
