package com.springboot_redis_demo.redis_demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.springboot_redis_demo.redis_demo.mapper")
public class RedisDemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(RedisDemoApplication.class, args);
    }

}
