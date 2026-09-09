package com.example.inspire_jpa.features.users.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.inspire_jpa.features.users.domain.dto.UserRequestDTO;
import com.example.inspire_jpa.features.users.domain.dto.UserResponseDTO;
import com.example.inspire_jpa.features.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


/* 
- Noun 작성 
GET     /api/v1/users
GET     /api/v1/users/{email} 
POST    /api/v1/users
PUT     /api/v1/users/{email}/{blogId}/
DELETE  /api/v1/users/{email}

1              :           N, 1            :             N
Users -------------------> Blogs ----------------------> Comments

/users/{email}/blogs
/blogs/{blogId}/comments 
*/

@Tag(name = "User API", description = "사용자 생성과 로그인 관련 API 명세서")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor 
public class UserController {
 
    private final UserService userService ;
    
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
                schema  = @Schema(implementation = UserRequestDTO.class) 
            ) 
        )   
        @RequestBody @Valid UserRequestDTO request, BindingResult bindingResult) {
        System.out.println("debug >>>> user controller signup ");
        System.out.println("debug >>>> user controller signup params : "+request); 
        System.out.println("debug >>>> user controller signup bindingResult : "+bindingResult); 

        if(bindingResult.hasErrors()) {
            System.out.println("debug >>>> user controller signup validation error ");
            
            // System.out.println("debug >>>> error message "); 
            // bindingResult.getFieldErrors()
            //     .stream()
            //     .map(FieldError::getDefaultMessage)
            //     .forEach(System.out::println);


            Map<String, String> errMap = new HashMap<>();
            bindingResult.getAllErrors().forEach( err -> {
                FieldError field    = (FieldError)err;
                String     message  = err.getDefaultMessage();
                errMap.put(field.getField(), message);
            });

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errMap); 
        }


        UserResponseDTO response = userService.signUp(request);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response) ;        
    }

    /* 
    추후 
    - Json Web Token (JWT) 
    - 인증(Authentication), 인가(Authorization)
    */
    @Operation(summary = "로그인", description = "사용자 로그인(email, password)")
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증 오류"),
        }
    )

    @GetMapping("/signIn")
    public ResponseEntity<?> signIn(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "사용자 로그인정보를 담는 DTO",
            required    = true,
            content     = @Content(
                schema  = @Schema(implementation = UserRequestDTO.class) 
            ) 
        )   
        @RequestParam("email")      String email, 
        @RequestParam("password")   String password ) {

        System.out.println("debug >>>> user controller signIn ");
        System.out.println("debug >>>> user controller signIn params : "+email+"\t"+password);
                
        Map<String, Object> map = userService.signIn(UserRequestDTO.builder()
                                                        .email(email)
                                                        .password(password).build()); 
        
        HttpHeaders headers = new HttpHeaders(); 
        headers.add("Authorization", "Bearer "+(String)(map.get("at")));
        headers.add("Refresh-Token", (String)(map.get("rt")));
        headers.add("Access-Control-Expose-Headers", "Authorization, Refresh-Token");
            
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body( (UserResponseDTO)(map.get("response"))) ; 
    }
    
}


