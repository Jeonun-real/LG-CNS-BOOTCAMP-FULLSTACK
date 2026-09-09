package com.example.inspire_jpa.features.commons.handler;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder 
@Getter 
@ToString 
public class ErrorResponse {
    
    private String message ;

}

