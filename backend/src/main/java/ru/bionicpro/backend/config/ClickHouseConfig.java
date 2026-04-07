package ru.bionicpro.backend.config;

import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
public class ClickHouseConfig {

  @Value("${clickhouse.url}")
  private String url;

  @Value("${clickhouse.user}")
  private String user;

  @Value("${clickhouse.password}")
  private String password;

  @Bean
  public JdbcTemplate clickHouseJdbcTemplate() throws SQLException {
    Properties props = new Properties();
    props.setProperty("user", user);
    props.setProperty("password", password);

    ClickHouseDataSource dataSource = new ClickHouseDataSource(url, props);
    return new JdbcTemplate(dataSource);
  }
}