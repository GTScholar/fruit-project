package com.fruitfresh.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / Swagger 接口文档配置
 * 访问地址：http://localhost:8080/api/doc.html
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("果漾鲜生 FruitFresh 接口文档")
                .description("水果销售平台：包含用户端与管理员端全部接口")
                .version("1.0.0")
                .contact(new Contact().name("FruitFresh")));
    }
}
