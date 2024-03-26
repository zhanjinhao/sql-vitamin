package cn.addenda.sql.vitamins.test.rewriter.baseentity;

import cn.addenda.sql.vitamins.rewriter.baseentity.BaseEntityRewriter;
import cn.addenda.sql.vitamins.rewriter.baseentity.DefaultBaseEntitySource;
import cn.addenda.sql.vitamins.rewriter.baseentity.DruidBaseEntityRewriter;
import cn.addenda.sql.vitamins.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamins.rewriter.visitor.item.InsertAddSelectItemMode;
import cn.addenda.sql.vitamins.rewriter.visitor.item.UpdateItemMode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author addenda
 * @since 2023/9/11 10:50
 */
@Slf4j
public class DruidBaseEntityRewriterShowCaseTest {

  @Test
  public void testInsert1() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
    String sql = "insert into score ( SNO, CNO, DEGREE ) values ( 109, '\\\\3-105', 76)";

    String s = baseEntityRewriter.rewriteInsertSql(sql,
        InsertAddSelectItemMode.DB, false, UpdateItemMode.NOT_NULL, false);

    //  insert into score (SNO, CNO, DEGREE,
    //       creator, creator_name, create_time, modifier, modifier_name, modify_time, remark)
    //  values (109, '\\3-105', 76,
    //       'addenda', 'addenda', now(3), 'addenda', 'addenda', now(3), null)
    log.info(s);
  }

  @Test
  public void testInsert2() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
    String sql = "insert  into score set SNO=109, CNO='\\\\3-105', DEGREE=76";

    String s = baseEntityRewriter.rewriteInsertSql(sql,
        InsertAddSelectItemMode.DB, false, UpdateItemMode.NOT_NULL, false);

    //  insert into score (SNO, CNO, DEGREE,
    //        creator, creator_name , create_time, modifier, modifier_name, modify_time, remark)
    //  values (109, '3-105', 76,
    //        'addenda', 'addenda' , now(3), 'addenda', 'addenda', now(3), null)
    log.info(s);
  }

  @Test
  public void testInsert3() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
    String sql = "insert into t_cdc_test(long_d, int_d, string_d, date_d, time_d, datetime_d, float_d, double_d) "
        + "values (? + 1, ?, replace(?,'a','b'), date_add(?, interval 1 day), ?, now(), ?, ?)";

    String s = baseEntityRewriter.rewriteInsertSql(sql,
        InsertAddSelectItemMode.DB, false, UpdateItemMode.NOT_NULL, false);

    //  insert into t_cdc_test (long_d, int_d, string_d, date_d, time_d, datetime_d, float_d, double_d,
    //        creator, creator_name, create_time, modifier, modifier_name, modify_time, remark)
    //  values (? + 1, ?, replace(?, 'a', 'b'), date_add(?, interval 1 day), ?, now(), ?, ?,
    //        'addenda', 'addenda', now(3), 'addenda', 'addenda', now(3), null)
    log.info(s);
  }

  @Test
  public void testUpdate1() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
    String sql = "update runoob_tbl set runoob_title=replace( runoob_title , 'c++', 'python' ) , a=?  + 1 , b=?";

    String s = baseEntityRewriter.rewriteUpdateSql(sql, UpdateItemMode.NOT_NULL, false);

    //  update runoob_tbl set runoob_title = replace(runoob_title, 'c++', 'python'), a = ? + 1, b = ?,
    //      modifier = 'addenda', modifier_name = 'addenda', modify_time = now(3)
    log.info(s);
  }

  @Test
  public void testUpdate2() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());
    String sql = "update runoob_tbl set runoob_title= replace( runoob_title , 'c++', 'python' ) where id in (select outer_id from A)";

    String s = baseEntityRewriter.rewriteUpdateSql(sql, UpdateItemMode.NOT_NULL, false);

    //  update runoob_tbl set runoob_title = replace(runoob_title, 'c++', 'python'),
    //      modifier = 'addenda', modifier_name = 'addenda', modify_time = now(3) where id in ( select outer_id from A )
    log.info(s);
  }

  @Test
  public void testSelect1() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());

    String sql = "select a from b where c > 1";

    String s = baseEntityRewriter.rewriteSelectSql(sql, null);

    // select a, b.creator as creator, b.creator_name as creator_name, b.create_time as create_time, b.modifier as modifier ,
    //  b.modifier_name as modifier_name, b.modify_time as modify_time, b.remark as remark
    //  from b where c > 1
    log.info(s);
  }

  @Test
  public void testSelect2() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());

    String sql = "select customername , orderdate , lead ( orderdate  , 1  ) over ( partition by customernumber  order by orderdate   )   as nextorderdate from "
        + "orders join customers";

    String s = baseEntityRewriter.rewriteSelectSql(sql, null);

    // select customername, orderdate , lead(orderdate, 1) over (partition by customernumber order by orderdate) as nextorderdate ,
    //   orders.creator as orders_creator, customers.creator as customers_creator,
    //   orders.creator_name as orders_creator_name, customers.creator_name as customers_creator_name,
    //   orders.create_time as orders_create_time , customers.create_time as customers_create_time,
    //   orders.modifier as orders_modifier, customers.modifier as customers_modifier,
    //   orders.modifier_name as orders_modifier_name, customers.modifier_name as customers_modifier_name ,
    //   orders.modify_time as orders_modify_time, customers.modify_time as customers_modify_time,
    //   orders.remark as orders_remark, customers.remark as customers_remark
    // from orders join customers
    log.info(s);
  }

  @Test
  public void testSelect3() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());

    String sql = "select customername , orderdate , lead ( orderdate  , 1  ) over ( partition by customernumber  order by orderdate   )   as nextorderdate from "
        + "orders join customers";

    String s = baseEntityRewriter.rewriteSelectSql(sql, "orders");

    // select customername, orderdate , lead(orderdate, 1) over (partition by customernumber order by orderdate) as nextorderdate ,
    //   orders.creator as creator, customers.creator as customers_creator,
    //   orders.creator_name as creator_name, customers.creator_name as customers_creator_name,
    //   orders.create_time as create_time , customers.create_time as customers_create_time,
    //   orders.modifier as modifier, customers.modifier as customers_modifier,
    //   orders.modifier_name as modifier_name, customers.modifier_name as customers_modifier_name ,
    //   orders.modify_time as modify_time, customers.modify_time as customers_modify_time,
    //   orders.remark as remark, customers.remark as customers_remark
    // from orders join customers
    log.info(s);
  }

  @Test
  public void testSelect4() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());

    String sql = "select * from t_a left join t_b on t_a.b_id = t_b.id";

    String s = baseEntityRewriter.rewriteSelectSql(sql, null);

    // select *,
    //   t_a.creator as creator, t_b.creator as t_b_creator,
    //   t_a.creator_name as creator_name, t_b.creator_name as t_b_creator_name ,
    //   t_a.create_time as create_time, t_b.create_time as t_b_create_time,
    //   t_a.modifier as modifier, t_b.modifier as t_b_modifier,
    //   t_a.modifier_name as modifier_name , t_b.modifier_name as t_b_modifier_name,
    //   t_a.modify_time as modify_time, t_b.modify_time as t_b_modify_time,
    //   t_a.remark as remark, t_b.remark as t_b_remark
    // from t_a left join t_b on t_a.b_id = t_b.id
    log.info(s);
  }

  @Test
  public void testSelect5() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());

    String sql = "select * from t_a right join t_b on t_a.b_id = t_b.id";

    String s = baseEntityRewriter.rewriteSelectSql(sql, null);

    // select *,
    //   t_a.creator as t_a_creator, t_b.creator as creator,
    //   t_a.creator_name as t_a_creator_name, t_b.creator_name as creator_name ,
    //   t_a.create_time as t_a_create_time, t_b.create_time as create_time,
    //   t_a.modifier as t_a_modifier, t_b.modifier as modifier,
    //   t_a.modifier_name as t_a_modifier_name , t_b.modifier_name as modifier_name,
    //   t_a.modify_time as t_a_modify_time, t_b.modify_time as modify_time,
    //   t_a.remark as t_a_remark, t_b.remark as remark
    // from t_a right join t_b on t_a.b_id = t_b.id
    log.info(s);
  }


  @Test
  public void testSelect6() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());

    String sql = "  select 1 from (select a  from d11  d1 where age > 10) t1 left join d33 d3 where  (  select 2 from d44 )  > t1.a";

    String s = baseEntityRewriter.rewriteSelectSql(sql, null);

    // select 1,
    //   t1.d1_creator as creator, d3.creator as d3_creator,
    //   t1.d1_creator_name as creator_name, d3.creator_name as d3_creator_name ,
    //   t1.d1_create_time as create_time, d3.create_time as d3_create_time,
    //   t1.d1_modifier as modifier, d3.modifier as d3_modifier,
    //   t1.d1_modifier_name as modifier_name , d3.modifier_name as d3_modifier_name,
    //   t1.d1_modify_time as modify_time, d3.modify_time as d3_modify_time,
    //   t1.d1_remark as remark, d3.remark as d3_remark
    // from (
    //      select a,
    //          d1.creator as d1_creator,
    //          d1.creator_name as d1_creator_name,
    //          d1.create_time as d1_create_time,
    //          d1.modifier as d1_modifier ,
    //          d1.modifier_name as d1_modifier_name,
    //          d1.modify_time as d1_modify_time,
    //          d1.remark as d1_remark
    //      from d11 d1 where age > 10 ) t1 left join d33 d3 where ( select 2 from d44 ) > t1.a
    log.info(s);
  }

  @Test
  public void testSelect7() {
    BaseEntityRewriter baseEntityRewriter =
        new DruidBaseEntityRewriter(null, null, new DefaultBaseEntitySource(), new DefaultDataConvertorRegistry());

    String sql = "select b from a1 group by b, create_time, remark having count(*) > 1 and c > 2";

    String s = baseEntityRewriter.rewriteSelectSql(sql, null);

    // select b,
    //   a1.create_time as create_time,
    //   a1.remark as remark from a1 group by b, create_time, remark having count(*) > 1 and c > 2
    log.info(s);
  }

}
