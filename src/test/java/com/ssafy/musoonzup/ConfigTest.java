package com.ssafy.musoonzup;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionManager;

@SpringBootTest
@Slf4j
public class ConfigTest {

  @Autowired
  DataSource ds;

  @Autowired
  SqlSessionTemplate template;

  @Autowired
  TransactionManager tm;

  @Test
  public void dataSourceTest() throws Exception {
    assertNotNull(ds);
//    log.trace("connection: {}", ds.getClass().getName());
    if (ds instanceof HikariDataSource hds) {
      Assertions.assertEquals(5, hds.getMaximumPoolSize());
      Assertions.assertEquals(3, hds.getMinimumIdle());
      Assertions.assertEquals(1000 * 60 * 5, hds.getIdleTimeout());
      Assertions.assertEquals(1000 * 60 * 10, hds.getConnectionTimeout());
    }
    assertNotNull(template);
  }

  @Test
  public void infraBeanTest() {
    assertNotNull(template);
    assertNotNull(tm);
  }

  @Autowired
  ApplicationContext context;

  @Test
  void test() {
    System.out.println(context.containsBean("memberDao"));
  }

}
