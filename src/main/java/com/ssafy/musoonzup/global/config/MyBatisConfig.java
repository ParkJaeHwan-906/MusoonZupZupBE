package com.ssafy.musoonzup.global.config;

import com.ssafy.musoonzup.global.interceptor.TimestampInterceptor;
import javax.sql.DataSource;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


@SpringBootConfiguration
@MapperScan("com.ssafy.musoonzup.*.dao") // Mapper 인터페이스 위치
public class MyBatisConfig {

  @Bean
  public TimestampInterceptor timestampInterceptor() {
    return new TimestampInterceptor();
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
    factory.setDataSource(dataSource);

    // MyBatis 설정 객체 생성 및 카멜 케이스 자동 매핑 설정
    Configuration myBatisConfig = new Configuration();
    myBatisConfig.setMapUnderscoreToCamelCase(true); // snake_case → camelCase
    factory.setConfiguration(myBatisConfig);

    // Interceptor 등록
    factory.setPlugins(timestampInterceptor());

    // XML Mapper 경로 설정
    factory.setMapperLocations(
        new PathMatchingResourcePatternResolver().getResources("classpath:/mappers/**/*.xml")
    );

    return factory.getObject();
  }
}

