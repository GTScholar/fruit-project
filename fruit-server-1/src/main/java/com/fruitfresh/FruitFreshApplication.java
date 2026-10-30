package com.fruitfresh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 果漾鲜生 FruitFresh 水果销售平台 启动类
 *
 * @author fruitfresh
 */
@SpringBootApplication
@MapperScan("com.fruitfresh.mapper")
@EnableScheduling
public class FruitFreshApplication {

    public static void main(String[] args) {
        SpringApplication.run(FruitFreshApplication.class, args);
        System.out.println("""
                -----------------------------------------------------------
                  果漾鲜生 FruitFresh 启动成功
                  接口文档: http://localhost:8080/api/doc.html
                -----------------------------------------------------------""");
    }
}
