package com.example.inspire_mybatis.features.users.ctrl;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.inspire_mybatis.features.users.domain.dto.UserRequestDTO;
import com.example.inspire_mybatis.features.users.domain.dto.UserResponseDTO;
import com.example.inspire_mybatis.features.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@Tag(name = "User API", description = "사용자 생성과 로그인 관련 API 명세서")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "신규가입(email, password, name)")
    @ApiResponses(
        {
            @ApiResponse(responseCode = "201", description = "가입성공"),
            @ApiResponse(responseCode = "400", description = "유효성검사실패"),
            @ApiResponse(responseCode = "500", description = "가입실패")
        }
    )
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "사용자 정보를 담는 DTO",
            required    = true,
            content     = @Content(
            schema      = @Schema(implementation = UserRequestDTO.class) 
            ) 
        )
        @RequestBody UserRequestDTO request) {
        System.out.println("debug >>>> user controller signup ");
        System.out.println("debug >>>> user controller signup params : "+request); 
        int signUpFlag = userService.SingUp(request);
        if(signUpFlag !=0){
            return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(null);
        }else{
            return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null);
        }
    }
    



    /*
    추후
    - json web token(jwt)
    - 인증(authentificaiton), 인가(authorization) 
    */
    @Operation(summary = "로그인", description = "사용자 로그인(email, password)")
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패"),
        }
    )
    @GetMapping("/signIn")
    public ResponseEntity<?> singIn(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "사용자 가입정보를 담는 DTO",
            required    = true,
            content     = @Content(
                schema  = @Schema(implementation = UserRequestDTO.class) 
            ) 
        )
        @RequestParam("email") String email,
        @RequestParam("password") String password) {
        System.out.println("debug >>>> user controller signup ");
        System.out.println("debug >>>> user controller signup params : "+email); 
        System.out.println("debug >>>> user controller signup params : "+password); 

        
        Map<String, Object> map = userService.signIn(UserRequestDTO.builder()
                                                                .email(email)
                                                                .password(password)
                                                                .build());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",(String)(map.get("at")));
        headers.add("Refresh-Token",(String)(map.get("rt")));
        headers.add("Access-Control-Expose-Headers", "Authorization, Refresh-Token");

        return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body((UserResponseDTO)(map.get("response")));
    }


}
    


