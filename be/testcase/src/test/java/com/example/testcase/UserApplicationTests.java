package com.example.testcase;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.testcase.features.users.domain.dto.UserRequestDTO;
import com.example.testcase.features.users.domain.dto.UserResponseDTO;
import com.example.testcase.features.users.repository.UserMapper;

//junit + TDD(Test Driven Development : given when then)

@SpringBootTest
public class UserApplicationTests {          
    @Autowired
    private UserMapper userMapper; // mybatis를 구현하는 interface


    @Test
    public void signUp(){
        System.out.println("usserMapper: " + userMapper);
        
        //given - 데이터준비
        UserRequestDTO request = 
            UserRequestDTO.builder()
                .email("tjwjddns1302@gmail.com")
                .password("root")
                .name("tjwddns")
                .build();

        //when - 테스트 실행
        int flag = userMapper.save(request);

        //then - 테스트 검증
        System.out.println("result: " + flag);
        Assertions.assertEquals(1, flag); // 1이면 성공, 0이면 실패
    }


    @Test
    public void list(){

        //관리자로서 회원의 전체정보를 조회
        // given - 데이터준비
        // when - 테스트 실행
        List<UserResponseDTO> list = userMapper.findByAll();

        //then
        list.stream()
            .forEach(System.out::println);
    }

    @Test
    public void signIn(){
        System.out.println("사용자 로그인 검증");
        
        //given
        UserRequestDTO request = 
            UserRequestDTO.builder()
                .email("tjwjddns1302@gmail.com")
                .password("root")
                .build();
        //when
        Optional<UserResponseDTO> response = userMapper.login(request);

        // response.orElseThrow(() -> 
        //     new RuntimeException("이메일 또는 패스워드 불일치"));

        //then
        Assertions.assertNotNull(response.get());
        Assertions.assertEquals("tjwjddns1302@gmail.com", response.get().getEmail());
    }
}
