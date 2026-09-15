package com.example.inspire_jpa.features.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration 
public class SwaggerConfig {
    @Bean
    public OpenAPI customerOpenAPI() {
        // Security Schema 정의 
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization") ; 

        // Security Requirement 
        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("BearerAuth") ;


        return new OpenAPI()
                    .addSecurityItem(securityRequirement)
                    .schemaRequirement("BearerAuth", securityScheme) ;

    }
}
