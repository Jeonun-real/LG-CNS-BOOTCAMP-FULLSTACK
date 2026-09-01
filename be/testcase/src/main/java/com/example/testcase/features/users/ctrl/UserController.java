package com.example.testcase.features.users.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.testcase.features.users.domain.dto.UserRequestDTO;
import com.example.testcase.features.users.service.UserEncryptionServiceImpl;
import com.example.testcase.features.users.service.UserService;

import jakarta.annotation.Resource;

@RestController

// endPoint : http://id:port/users
@RequestMapping("/users")
public class UserController {
    
    @Resource(name = "encryption")
    private UserService userService;

    //ResponseEntity<?> => header(token) status(code) body(data)
    //endPoint : http://id:port/users/signIn : method(Get)
    //http://id:port/users/signIn ? email=xxxxxx&password=xxxxxx
    @GetMapping("/signIn")
    public ResponseEntity<?> signIn(UserRequestDTO request){
        System.out.println("UserController signIn get mapping");
        System.out.println("UserController signIn params: " + request);  
        System.out.println("UserController servicebinding " + userService);  

        return null;
    }

}
