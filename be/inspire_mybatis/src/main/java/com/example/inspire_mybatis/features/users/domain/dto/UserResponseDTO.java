package com.example.inspire_mybatis.features.users.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class UserResponseDTO {
    private String email, password, name;
}
