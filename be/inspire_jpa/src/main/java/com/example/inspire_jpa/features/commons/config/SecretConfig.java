package com.example.inspire_jpa.features.commons.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.inspire_jpa.features.commons.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor 
public class SecretConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // hashing 위한 객체 (복호화X)
    @Bean 
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

   @Bean 
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration() ;

        configuration.setAllowedOrigins(List.of(   "http://localhost:3200", "http://127.0.0.1:3200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Refresh-token"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source ;

    }

    @Bean
    public SecurityFilterChain filterchain(HttpSecurity http) throws Exception {
        System.out.println("securityconfig filterchain");

        //cors vs csrf(cross-site request forgery)
        //csrf: 사용자가 의도하지 않은 요청을 해커가 사용자가 요청하는 것처럼 보내는것
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                        "/users/**",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/openai/**",
                                    "/openapi/**").permitAll() 
                                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                    .requestMatchers("/admin/**").hasRole("ADMIN")  
                                 .anyRequest().authenticated()
                        ).sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
