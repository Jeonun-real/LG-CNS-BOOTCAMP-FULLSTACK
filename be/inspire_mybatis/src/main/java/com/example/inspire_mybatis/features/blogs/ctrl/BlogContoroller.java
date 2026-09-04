package com.example.inspire_mybatis.features.blogs.ctrl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.inspire_mybatis.features.blogs.domain.dto.BlogRequestDTO;
import com.example.inspire_mybatis.features.blogs.domain.dto.BlogResponseDTO;
import com.example.inspire_mybatis.features.blogs.service.BlogService;
import com.example.inspire_mybatis.features.users.domain.dto.UserRequestDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;



@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogContoroller {
    
    private final BlogService blogService;


    @GetMapping("/index")
    public ResponseEntity<?> index() {
        System.out.println(">>>>blog controller index");
        List<BlogResponseDTO> list = blogService.list();

        //status code : no_content(204), ok(200)
        // return list.isEmpty()
        //             ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        //             : ResponseEntity.status(HttpStatus.OK).body(list);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody BlogRequestDTO request) {
        System.out.println(">>>>blog insert controller");
        System.out.println(">>>>blog insert param:"+ request);
        int insertFlag = blogService.insert(request);
        if(insertFlag !=0){
        return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(null);
        }else{
            return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null);
        }
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable("id") Integer id,
                                  @RequestHeader("Authorization") String at) {
        System.out.println(">>>>blog read controller");
        System.out.println(">>>>blog read param:"+ id);
        System.out.println(">>>>blog controller read access token:"+ at);

        BlogResponseDTO response = blogService.read(id);
        System.out.println(">>>>vlog controller read result"+response);

        // status code : not_found(404), ok(200)
        return response != null
            ? ResponseEntity.status(HttpStatus.OK).body(response) 
            : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    
    
}
