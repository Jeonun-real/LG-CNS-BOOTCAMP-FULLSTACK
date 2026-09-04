package com.example.inspire_mybatis.features.users.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor


/* 
validation Annotation
@notnull, @notempty, @notblank @pattern정규표현식 @email etc
*/
public class UserRequestDTO {

    @Email(message = "이메일 형식을 지켜주세요!!")
    @NotBlank(message = "이메일은 필수항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    @Size(min=4,max=20, message = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다.")
    // @Pattern(regexp = "^(?=.[a-z])(?=.[A-Z])(?=.*\d)[A-Za-z\d]+$", message="패스워드 정책에 맞지 않습니다.")
    private String password;

    @NotBlank(message = "이름은 필수항목입니다.")
    private String name;

    //optional (spring security role) 옵션으로 사용가능
    // private String role;
}
