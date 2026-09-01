package com.example.testcase.features.users.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.example.testcase.features.users.domain.dto.UserRequestDTO;
import com.example.testcase.features.users.domain.dto.UserResponseDTO;

@Mapper
// xxxx.Maper.java: mybatix를 구현하는 interface
public interface UserMapper {
    public int                      save(UserRequestDTO request);
    
    public List<UserResponseDTO>     findByAll();

    public Optional<UserResponseDTO> login(UserRequestDTO request);
}

