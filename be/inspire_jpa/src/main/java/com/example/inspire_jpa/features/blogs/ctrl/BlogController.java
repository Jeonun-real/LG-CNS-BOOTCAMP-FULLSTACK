package com.example.inspire_jpa.features.blogs.ctrl;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inspire_jpa.features.blogs.ai.agent.BlogAIAgent;
import com.example.inspire_jpa.features.blogs.domain.dto.BlogRequestDTO;
import com.example.inspire_jpa.features.blogs.domain.dto.BlogResponseDTO;
import com.example.inspire_jpa.features.blogs.service.BlogService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/blogs")
@RequiredArgsConstructor 
public class BlogController {
    
    private final BlogService blogService ; 
    private final BlogAIAgent blogAgent ; 



    // endPoint -> http:// ip : port / blogs / index 
    @GetMapping("/index")
    public ResponseEntity<?> index() {
        System.out.println("debug >>>> blog controller index ");
        List<BlogResponseDTO> list = blogService.list();

        // status code : NO_CONTENT(204), OK(200)
        // return list.isEmpty()
        //         ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        //         : ResponseEntity.status(HttpStatus.OK).body(list);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody BlogRequestDTO request) {
        System.out.println("debug >>>> blog controller insert ");
        System.out.println("debug >>>> blog controller insert params : "+request);
        
        //기존 insert
        // BlogResponseDTO response = blogService.insert(request);

        //agent 변경
        BlogResponseDTO response = blogAgent.insert(request);

        System.out.println("debug >>>> blog controller insert result flag : "+response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response) ;
               
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(  @PathVariable("id") Integer id,
                                    @RequestHeader("Authorization") String at) {
        System.out.println("debug >>>> blog controller read ");
        System.out.println("debug >>>> blog controller read accecc token : "+at); 
        System.out.println("debug >>>> blog controller read params : "+id);


        BlogResponseDTO response = blogService.read(id); 
        System.out.println("debug >>>> blog controller read result : "+response); 

        // status code : NOT_FOUND(404), OK(200)
        return ResponseEntity.status(HttpStatus.OK).body(response) ;
                
        
    }
    
    @PostMapping("/ai/agent")
    public ResponseEntity<?> agent(@RequestBody Map<String, Object> map) {
        System.out.println("debug >>>> blog controller agent ");
        System.out.println("debug >>>> blog controller read params : "+ map.get("category"));
        System.out.println("debug >>>> blog controller read params : "+ map.get("keyword"));

        // 기존 CRUD
        // return ResponseEntity
        //         .status(HttpStatus.CREATED)
        //         .body(blogService.contentGenerate(map));

        //AI AGNET
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(blogAgent.generate(map));
    }
    
    
    
}

