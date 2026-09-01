package com.example.testcase.features.users.service;

import org.springframework.stereotype.Service;

import com.example.testcase.features.users.domain.dto.UserRequestDTO;
import com.example.testcase.features.users.domain.dto.UserResponseDTO;


@Service
public class UserEncryptionServiceImpl implements UserService {
    
    @Override
    public UserResponseDTO signIn(UserRequestDTO request) {
        System.out.println("EncryptionServiceImpl signIn");
        System.out.println("params" + request);
        return null;
    }    
}
