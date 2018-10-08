package com.xbb;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.Properties;

/**
 * @Auther: xbb
 * @Date: 2018-9-13 15:47
 * @Description:
 */
@Configuration
public class AppConfig {

    @Bean(value = "appBeanFactory")
    public AppBeanFactory getAppBeanFactory(){
        AppBeanFactory appBeanFactory=new AppBeanFactory();
        return appBeanFactory;
    }

    @Bean(value = "dataSource")
    public DruidDataSource getDruidDataSource(){
        DruidDataSource dataSource=new DruidDataSource();
        Properties properties=new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("datasource.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataSource.setUrl(properties.getProperty("jdbc.url"));
        dataSource.setDriverClassName(properties.getProperty("driverClassName"));
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(20);
        dataSource.setMinIdle(4);
        dataSource.setMaxWait(60000);
        return dataSource;
    }

    @Bean(value = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate(){
        JdbcTemplate jdbcTemplate=new JdbcTemplate();
        jdbcTemplate.setDataSource(getDruidDataSource());
        return jdbcTemplate;
    }



}
