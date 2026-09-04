package com.example.inspire_mybatis.features.commons.exception.users;

public class LoginFailException extends RuntimeException {

    public LoginFailException(){

    }

    public LoginFailException(String message){
        super(message);
    }
    
}
