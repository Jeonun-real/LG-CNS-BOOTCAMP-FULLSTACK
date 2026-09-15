package com.example.inspire_jpa.features.commons.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    @Value("${jwt.secret}") 
    private String  secret ; 
    private Key     key ; 

    @PostConstruct
    private void init() {
        System.out.println("debug >>>> JwtAuthenticationFilter secret: "+secret); 
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
    throws ServletException, IOException {

        System.out.println("debug >>>> JwtAuthenticationFilter doFilter"); 

        String endPoint = request.getRequestURI() ;
        System.out.println("debug >>>> JwtAuthenticationFilter user endPoint : "+endPoint); 
        String method   = request.getMethod() ;
        System.out.println("debug >>>> JwtAuthenticationFilter user method : "+method); 

        // preflight : get,post -> options 전달이 이루어짐 
        // if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        //     System.out.println("debug >>>> JwtAuthenticationFilter preflight"); 


            // header set : Origin, Method, Header 

            // security config로 처리 예쩡
            // String origin = req.getHeader("Origin");
            // res.setStatus(HttpServletResponse.SC_OK); 
            // if ("http://localhost:3200".equals(origin)
            //         || "http://127.0.0.1:3200".equals(origin)) {
            //     res.setHeader("Access-Control-Allow-Origin", origin);
            // }
            // res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT,PATCH, DELETE, OPTIONS");
            // res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Refresh-token");
            // res.setHeader("Access-Control-Allow-Credentials", "true");
        //     filterChain.doFilter(request, response);
        //     return ;
    
        // }

        String header = request.getHeader("Authorization");
        System.out.println("debug >>>> JwtFilter header "+header); 
        if( header == null || !header.startsWith("Bearer ")) {
            System.out.println("debug >>>> JwtFilter UNAUTHORIZED "); 
            filterChain.doFilter(request, response);
            return ;
        }

        String token = header.substring(7);
        System.out.println("debug >>>> JwtFilter token validation pass"); 
        System.out.println("debug >>>> JwtFilter token "+token); 

        // 데이터를 가져오고 이를 CLAIMS에 저장
        try {
            Claims claims = Jwts.parserBuilder()
                                    .setSigningKey(key)
                                    .build()
                                    .parseClaimsJws(token)
                                    .getBody();

            String email = claims.getSubject();
            System.out.println(">>>> JwtAuthenticationFilter token claim, get email " + email);

            //role
            String role = claims.get("role", String.class);
            System.out.println(">>>> JwtAuthenticationFilter token rogle, get role " + role);
 
            //security 인증정보를 담는 객체
            //pricipal, credentrial,authorities
            // UsernamepasswordauthenticationToken
            // SecurityContextHolder <- user id(email)
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    email,                                  // principal: 인증된 사용자
                    null,                                   // credentials: 비밀번호 등 인증 정보
                    role != null
                    ? java.util.List.of(() -> "ROLE_" + role)
                    : java.util.List.of()
                );
                

            // 요청한 사용자의 인증정보객체를 연동
            authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)    
            );

            //사용자의 정보를 securitycontextholder에 저장하고
            //필요한 경우 controller 또는 service에 로드하여 정보를 비교할 수 있도록
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println(">>>>JwtAuthenticationFilter security context holder save");
            filterChain.doFilter(request, response);
            
            
        }catch(Exception e) {
            e.printStackTrace();
            // System.out.println(HttpServletRequest.SC_UNAUTHORIZED); 
            return  ;    
        }



        

    }

    

}