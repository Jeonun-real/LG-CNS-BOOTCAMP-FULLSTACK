package com.example.inspire_jpa.features.commons.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

    // .yml 설정된 키 값을 사용할 수 있음. 
    @Value("${jwt.secret}") 
    private String secret ; 

    // ms 단위로 계산하는 것이 기본
    private final long ACCESS_TOKEN_EXPIRY  = 1000L * 60 * 30 ;
    private final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 7 ;
    
    private Key getSecretKey() {
        System.out.println("debug >>>> Provider jwt secret : "+secret); 
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    
    public String createAT(String email) {
        System.out.println("debug >>>> Provider createAT");

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRY))
                .signWith(getSecretKey())
                .compact() ; 
    }
    public String createRT(String email) {
        System.out.println("debug >>>> Provider createRT");
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+REFRESH_TOKEN_EXPIRY))
                .signWith(getSecretKey())
                .compact() ; 
    }

    // AT를 통해서 subject 추출해야하는 상황이 있다면?
    public String getUserEmailFromAT(String at) {
        System.out.println("debug >>>> Provider getUserEmailFromAT at : "+at);
        return null ; 
    }


}


