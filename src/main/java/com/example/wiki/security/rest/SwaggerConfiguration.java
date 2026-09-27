package com.example.wiki.security.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@ConditionalOnBean(SecurityRestConfiguration.class)
@Configuration
public class SwaggerConfiguration {
    private static final String SCHEME_NAME = "bearerAuth";
    private static final String BEARER_FORMAT = "JWT";
    private static final String SCHEME = "bearer";

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat(BEARER_FORMAT)
                                .scheme(SCHEME)));
    }
}
