package com.example.inspire_mybatis.features.commons.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.inspire_mybatis.features.commons.exception.users.LoginFailException;

// spring framework 내부에 예외를 처리하는 전역 handler 들아 handlerExceptionResolver에 등록

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(LoginFailException.class)
    public ResponseEntity<?> handlerLoginFail(LoginFailException e){
        System.out.println(">>>> globalExceptionHandler hanlderLoginFail");
        System.out.println(">>>> e.message " +e.getMessage());
        
        ErrorResponse error = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error); 
    }

}
