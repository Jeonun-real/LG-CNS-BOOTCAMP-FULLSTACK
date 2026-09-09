package com.example.inspire_jpa;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import com.example.inspire_jpa.features.users.domain.dto.UserRequestDTO;
import com.example.inspire_jpa.features.users.domain.dto.UserResponseDTO;
import com.example.inspire_jpa.features.users.domain.entity.UserEntity;
import com.example.inspire_jpa.features.users.repository.UserRepository;


@SpringBootTest 
@Transactional 
public class UserApplicationTests {

    @Autowired 
    private UserRepository  userRepository ;

    @Autowired 
    private Environment     env ; 

    @Test 
    public void checkEnv() {
        System.out.println("debug >>>> DB Driver : "+env.getProperty("DB_DRIVER"));
        System.out.println("debug >>>> DB Driver : "+env.getProperty("DB_URL"));
        System.out.println("debug >>>> DB Driver : "+env.getProperty("DB_USER"));
        System.out.println("debug >>>> DB Driver : "+env.getProperty("DB_PASSWORD"));
    }


    @Test 
    public void signUp(){
        System.out.println("debug >>>> signUp ");
        System.out.println("debug >>>> signUp repository : "+userRepository);

        UserRequestDTO request = UserRequestDTO.builder()
            .email("admin@naver.com")
            .password("123456789")
            .name("임정섭")
            .role("admin")
            .build();
        
        UserEntity saveEntity = userRepository.save(UserRequestDTO.toEntity(request));
        System.out.println("debug >>>> entity : "+saveEntity);  
        System.out.println("debug >>>> response : "+UserResponseDTO.fromEntity(saveEntity)); 
    }

    @Test 
    public void signIn(){
        System.out.println("debug >>>> signIn ");
        System.out.println("debug >>>> signIN repository : "+userRepository);

        // select * from table where email = ? and password = ? ; 
        UserRequestDTO request = UserRequestDTO.builder()
            .email("admin@naver.com")
            .password("123456789")
            .build();
        
        Optional<UserEntity> optional = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());

        System.out.println("debug >>>> entity : "+optional.get());  
        System.out.println("debug >>>> response : "+UserResponseDTO.fromEntity(optional.get())); 
    }

}



