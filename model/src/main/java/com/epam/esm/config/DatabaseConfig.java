package com.epam.esm.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.epam.esm")
@PropertySource("classpath:db-${spring.profiles.active}.properties")
public class DatabaseConfig {
    @Value("${driver}")
    private String driverName;

    @Value("${dev.url}")
    private String url;

    @Value("${dev.username}")
    private String username;

    @Value("${dev.password}")
    private String password;

    @Value("${pool.min}")
    private int minPoolSize;

    @Value("${pool.max}")
    private int maxPoolSize;

    @Bean
    public DataSource mysqlDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxTotal(maxPoolSize);

        return dataSource;
    }
}
