package com.example.inspire_jpa.features.commons.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Spring security에서 cors 해결
// @Configuration 
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") 
            .allowedHeaders("*") // allow all headers for now, as we are still learning
            .allowedOriginPatterns(
                "http://localhost:3200",
                "http://127.0.0.1:3200"
            ) // URL of front-end origins
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
    }
}
