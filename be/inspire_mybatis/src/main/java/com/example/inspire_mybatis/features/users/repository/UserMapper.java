package com.example.inspire_mybatis.features.users.repository;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.example.inspire_mybatis.features.users.domain.dto.UserRequestDTO;
import com.example.inspire_mybatis.features.users.domain.dto.UserResponseDTO;

@Mapper
public interface UserMapper {

    public int                       save(UserRequestDTO request);
    
    public Optional<UserResponseDTO> signIn(UserRequestDTO request);

} 