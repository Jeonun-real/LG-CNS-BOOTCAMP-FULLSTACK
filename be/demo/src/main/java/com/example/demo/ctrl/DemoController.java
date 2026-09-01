package com.example.demo.ctrl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//사용자의 request(http://id:port/context/data)가 들어오면 ACTION 처리 CONTROLLER
//SERVICE가 등록

//컨테이너가 첫글자를 소문자로 객체를 생성하고 이를 컨테이너가 관리

/*
user의 endpoint : 
- http:// ip : port / demo / index
- http:// ip : port / demo / update
- http:// ip : port / demo / select
- http:// ip : port / demo / delete
- http:// ip : port / demo / insert
*/
@Controller // (DemoController demoController = new DemoController())
@RequestMapping("/demo") // http:// ip : port / demo
public class DemoController {
    
    
    @GetMapping("/index")// /index
    public String getMethodName(){
        System.out.println("user endpoint : /index");
        return "index";
    }
    
}
