package com.example.demo.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.MessageDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


//jsp 페이지를 반환x, data(json)
@RestController
// user endPoint : http://ip:port/context
@RequestMapping("/rest")

public class ReactController {

    @GetMapping("/index")
    public ResponseEntity<?> getMethodName() {
        System.out.println("react controller germapping /index");
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("access-token","12345")
            .body(MessageDTO.builder().message("테스트").build());
    }
    
}
