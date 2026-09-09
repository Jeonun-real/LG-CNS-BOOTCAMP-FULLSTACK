package com.example.inspire_jpa.features.users.service;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inspire_jpa.features.commons.exception.users.LoginFailException;
import com.example.inspire_jpa.features.commons.token.JwtProvider;
import com.example.inspire_jpa.features.users.domain.dto.UserRequestDTO;
import com.example.inspire_jpa.features.users.domain.dto.UserResponseDTO;
import com.example.inspire_jpa.features.users.domain.entity.UserEntity;
import com.example.inspire_jpa.features.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    // constructor injection 
    private final UserRepository    userRepository ;
    private final JwtProvider       jwtProvider ;

     
    @Transactional 
    public UserResponseDTO signUp(UserRequestDTO request) {
        System.out.println("debug >>>> user service signUp"); 

        // case 01
        UserEntity entity = UserRequestDTO.toEntity(request) ;
        return UserResponseDTO.fromEntity(userRepository.save(entity));

        // case 02 
        // Optional.of(request)
        //     .filter(req -> !userRepository.existsById(request.getEmail()))
        //     .map(req -> userRepository.save(req.toEntity(request)))
        //     .map(req -> UserResponseDTO.fromEntity(req))
        //     .orElseThrow(() -> new LoginFailException("User SignUp Fail!!"));  
    }


    @Transactional 
    public Map<String, Object> signIn(UserRequestDTO request) {
        System.out.println("debug >>>> user service signIn"); 
        
        
        // plain text version 
        UserEntity entity = userRepository
                                .findByEmailAndPassword(request.getEmail(), request.getPassword())
                                .orElseThrow(() -> new LoginFailException("SignIn Fail!!"));
        
        // hashing version
        // userRepository
        //     .findById(request.getEmail())
        //     .orElseThrow(() -> new LoginFailException("SignIn Fail!!"));
        // 암호화된 패스워드를 비교하는 구문 


        
        // 사용자 로그인이 정상적으로 수행되면 token 발급되어야 함.
        System.out.println("debug >>>> user service signIn token provider ");
        String at = jwtProvider.createAT(entity.getEmail());
        String rt = jwtProvider.createRT(entity.getEmail()); 



        // inMemory DB - Redis, H2 
        // at, rt 담아서 관리 - redis - docker 
        System.out.println("debug >>>> user service RT redis DB save"); 

        
        Map<String, Object> map = new HashMap<>();
        map.put("response", UserResponseDTO.fromEntity(entity));
        map.put("at", at);
        map.put("rt", rt); 

        return map ; 
    }

}
