package com.example.inspire_mybatis.features.users.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.inspire_mybatis.features.commons.token.JwtProvider;
import com.example.inspire_mybatis.features.users.domain.dto.UserRequestDTO;
import com.example.inspire_mybatis.features.users.domain.dto.UserResponseDTO;
import com.example.inspire_mybatis.features.users.repository.UserMapper;
import com.example.inspire_mybatis.features.users.repository.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;

    public int SingUp(UserRequestDTO request) {
        System.out.println("debug >>>> user service signup ");
        return userMapper.save(request);
    }

    public Map<String, Object> signIn(UserRequestDTO request){
        System.out.println("debug >>>> service signIn");
        
        UserResponseDTO response =
            userMapper
                .signIn(request)
                .orElseThrow(() -> new RuntimeException("로그인실패"));

        //사용자 로그인이 정상적으로 수행되면 token 발급
        System.out.println("sign in token provider");
        String at = jwtProvider.createAT(request.getEmail());
        String rt = jwtProvider.createRT(request.getEmail());

        //inmemory db - redis, h2
        //at, rt 담아서 관리- redis - docker
        Map<String,Object> map = new HashMap<>();
        map.put("response",response);
        map.put("at",at);
        map.put("rt",rt);

        return map;

    }

    
}
