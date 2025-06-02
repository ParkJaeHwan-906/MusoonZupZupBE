package com.ssafy.musoonzup.global.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {    
	private static final String SECURITY_SCHEME_NAME = "bearerAuth";
	 
	@Bean
    public OpenAPI getOpenApi() {
        return new OpenAPI()
        		 .openapi("3.0.1") 
        		.components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")         // 토큰 방식 명시
                                .bearerFormat("JWT")      // JWT 명시
                        ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .info(getInfo());
    }

    private Info getInfo() {
        return new Info()
                .title("musoonzup API 명세")
                .version("0.0.1")
                .description("musoonzup API 명세서입니다.");
    }

    @Bean
    public GroupedOpenApi groupOpenApi() {
        return GroupedOpenApi.builder()
                .group("musoonzup")
                .packagesToScan("com.ssafy.musoonzup") // << 반드시 controller 패키지 포함
                .build();
    }
}