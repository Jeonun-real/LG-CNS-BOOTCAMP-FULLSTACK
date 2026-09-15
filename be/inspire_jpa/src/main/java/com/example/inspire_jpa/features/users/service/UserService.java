package com.example.inspire_jpa.features.users.service;


import java.net.Authenticator;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inspire_jpa.features.commons.exception.users.LoginFailException;
import com.example.inspire_jpa.features.commons.redis.RedisService;
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
    private final RedisService      redisService;
    private final PasswordEncoder   passwordEncoder;

     
    @Transactional 
    public UserResponseDTO signUp(UserRequestDTO request) {
        System.out.println("debug >>>> user service signUp"); 


        // spring security password hashing
        // tobuilder()를 통해서 일부 수정할때 기존 객체를 복사해서 추가하는 리펙토링
        UserRequestDTO hashingDTO = request.toBuilder()
            .password(passwordEncoder.encode(request.getPassword()))
            .build();

        // case 01
        UserEntity entity = UserRequestDTO.toEntity(hashingDTO) ;
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
        // UserEntity entity = userRepository
        //                         .findByEmailAndPassword(request.getEmail(), request.getPassword())
        //                         .orElseThrow(() -> new LoginFailException("SignIn Fail!!"));
        
        // hashing version
        // 암호화된 패스워드를 비교하는 구문 
        UserEntity entity = userRepository
                                .findById(request.getEmail())
                                .orElseThrow(() -> new LoginFailException("SignIn Fail!!"));

        if (!passwordEncoder.matches(request.getPassword(),entity.getPassword())){
            throw new RuntimeException("poassword not matches");
        }


        
        // 사용자 로그인이 정상적으로 수행되면 token 발급되어야 함.
        System.out.println("debug >>>> user service signIn token provider ");
        String at = jwtProvider.createAT(entity.getEmail());
        String rt = jwtProvider.createRT(entity.getEmail()); 



        // inMemory DB - Redis, H2 
        // at, rt 담아서 관리 - redis - docker 
        System.out.println("debug >>>> user service RT redis DB save"); 
        redisService.saveToken(entity.getEmail(), rt);
        
        Map<String, Object> map = new HashMap<>();
        map.put("response", UserResponseDTO.fromEntity(entity));
        map.put("at", at);
        map.put("rt", rt); 

        return map ; 
    }

    public void signOut(){
        System.out.println("debug >>>> user service signOut"); 
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        System.out.println(">>>>JwtAuthenticationFilter security context holder save"+email);
        redisService.deleteToken(email);
    }

}
