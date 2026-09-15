package com.example.inspire_jpa.features.commons.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


//spring filter 사용으로 jwt 필더 사용 안 함.
// @Component 
public class JwtFilter implements  Filter{

    @Value("${jwt.secret}") 
    private String  secret ; 
    private Key     key ; 

    @PostConstruct
    private void init() {
        System.out.println("debug >>>> Provider jwt secret : "+secret); 
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // token 필요없는 endPoint 등록 
    private static final List<String> WHITE_LIST = List.of(
        "/users/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    );

    private final AntPathMatcher matcher = new AntPathMatcher();

    public boolean isPath(String path) {
        return WHITE_LIST.stream()
                .anyMatch(pattern -> matcher.match(pattern, path)); 
    }

	@Override
	public void doFilter(   ServletRequest  request, 
                            ServletResponse response, 
                            FilterChain chain) 
    throws IOException, ServletException {
		System.out.println("debug >>>> JwtFilter doFilter"); 

        HttpServletRequest  req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response ; 

        String endPoint = req.getRequestURI() ;
        System.out.println("debug >>>> JwtFilter user endPoint : "+endPoint); 
        String method   = req.getMethod() ;
        System.out.println("debug >>>> JwtFilter user method : "+method); 
        
        
        // preflight : get,post -> options 전달이 이루어짐 
        if("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            System.out.println("debug >>>> JwtFilter preflight"); 
            // header set : Origin, Method, Header 
            String origin = req.getHeader("Origin");
            res.setStatus(HttpServletResponse.SC_OK); 
            if ("http://localhost:3200".equals(origin)
                    || "http://127.0.0.1:3200".equals(origin)) {
                res.setHeader("Access-Control-Allow-Origin", origin);
            }
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT,PATCH, DELETE, OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Refresh-token");
            res.setHeader("Access-Control-Allow-Credentials", "true");

            return ;

        }

        if(isPath(endPoint)) {
            System.out.println("debug >>>> JwtFilter "+endPoint+" 는 토근없이 통과"); 
            chain.doFilter(request, response);
            return ; 
        }

        /*
        토큰 유효성 검증
        - white list 등록되지않은 endPoint 
        - request header 토근 존재 유무 및 유효성을 검증(만료, 서명, 발행자) 
        - Bearer xxxxxxxxxxxx ; 
        */
        String header = req.getHeader("Authorization");
        System.out.println("debug >>>> JwtFilter header "+header); 
        if( header == null || !header.startsWith("Bearer ")) {
            System.out.println("debug >>>> JwtFilter UNAUTHORIZED "); 
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return ;
        }

        // token 존재한다면?
        // token : header, payload(Claims), signature
        String token = header.substring(7);
        System.out.println("debug >>>> JwtFilter token validation pass"); 
        System.out.println("debug >>>> JwtFilter token "+token); 

        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            System.out.println("debug >>>> JwtFilter token validation success move to frontcontroller");
            chain.doFilter(request, response); 
            
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("debug >>>> JwtFilter token validation fail"); 
            return  ;    
        }


	}

    
}
