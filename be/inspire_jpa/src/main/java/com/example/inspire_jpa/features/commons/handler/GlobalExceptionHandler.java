package com.example.inspire_jpa.features.commons.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.inspire_jpa.features.commons.exception.users.LoginFailException;


// spring framework 내부에 예외를 처리하는 전역 Handler 들이 HandlerExceptionResolver 등록 

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(LoginFailException.class) 
    public ResponseEntity<?>  handlerLoginFail(LoginFailException e) {
        System.out.println("debug >>>> GlobalExceptionHandler handlerLoginFail ");
        System.out.println("debug >>>> e.message " + e.getMessage()); 

        ErrorResponse error = new ErrorResponse(e.getMessage());

        // code  : UNAUTHORIZED(401)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }   

}

