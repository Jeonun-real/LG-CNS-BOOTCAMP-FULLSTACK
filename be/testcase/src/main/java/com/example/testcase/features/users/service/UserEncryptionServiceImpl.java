package com.example.testcase.features.users.service;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.example.testcase.features.users.domain.dto.UserRequestDTO;
import com.example.testcase.features.users.domain.dto.UserResponseDTO;
import com.example.testcase.features.users.repository.UserMapper;

import lombok.RequiredArgsConstructor;

@Service(value = "encryption")
@RequiredArgsConstructor //멤버변수 중 상수만은 초기화
public class UserEncryptionServiceImpl implements UserService {
    
    private final UserMapper userMapper; //RequiredArgsConstructor 때문에 상수로


    @Override
    public UserResponseDTO signIn(UserRequestDTO request) {
        System.out.println("EncryptionServiceImpl signIn");
        System.out.println("params" + request);
        return userMapper.login(request)
            .orElseThrow(()->new RuntimeException("로그인 실패"));
    }    
}
