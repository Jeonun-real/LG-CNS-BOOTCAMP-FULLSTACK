package com.example.inspire_jpa.features.users.domain.dto;

import com.example.inspire_jpa.features.users.domain.entity.UserEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class UserResponseDTO {

    private String email, password, name, role ;

    // JPA는 UserEntity 기반으로 데이터의 영속성을 관리 DTO -> Entity , Entity -> DTO 
    public static UserResponseDTO fromEntity(UserEntity entity) {
        return UserResponseDTO.builder()
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .role(entity.getRole())
                .build() ;
    }
}

