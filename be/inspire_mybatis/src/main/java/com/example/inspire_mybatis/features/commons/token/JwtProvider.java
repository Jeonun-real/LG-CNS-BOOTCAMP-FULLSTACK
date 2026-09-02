package com.example.inspire_mybatis.features.commons.token;

import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    
    public String createAT(String email){
        return "Bearer XXXXXXXXX";
    }
        public String createRT(String email){
            return "XXXXXXXXXXXXXXXXX";
    }
}
