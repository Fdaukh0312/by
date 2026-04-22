package com.kedi.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kedi.shop.mapper")
public class KediShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(KediShopApplication.class, args);
    }
}
