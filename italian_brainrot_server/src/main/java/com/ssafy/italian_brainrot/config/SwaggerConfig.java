package com.ssafy.italian_brainrot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Italian Brainrot Card Store API")
                        .description("SSAFY 13기 최종 관통 프로젝트\n\nItalian Brainrot Card Store API 문서")
                        .version("v1.0.0"));
    }
}