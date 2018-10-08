package com.xbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 *
 * @Description  服务启动
 * @Author       xieb
 * @Date         9:28 2018-9-10
 *
 **/
@SpringBootApplication
@ComponentScan("com.xbb")
public class AppSatrt {
    public static void main(String[] args) {
        SpringApplication.run(AppSatrt.class);
    }
}
