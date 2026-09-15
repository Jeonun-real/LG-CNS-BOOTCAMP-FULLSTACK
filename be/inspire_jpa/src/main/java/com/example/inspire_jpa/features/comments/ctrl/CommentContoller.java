package com.example.inspire_jpa.features.comments.ctrl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inspire_jpa.features.comments.domain.dto.CommentRequestDTO;
import com.example.inspire_jpa.features.comments.domain.dto.CommentResponseDTO;
import com.example.inspire_jpa.features.comments.service.CommentService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentContoller {

    private final CommentService commentService;


    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody CommentRequestDTO request) {
        System.out.println(">>>>comment insert controller");
        System.out.println(">>>>blog read param:"+ request);
        
        CommentResponseDTO response = commentService.insert(request);

        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id){
        System.out.println(">>>>comment delete controller");
        System.out.println(">>>>comment delete param:"+ id);
        
        int deleteFlag = commentService.delete(id);
        
        System.out.println(">>>>comment controller delete result flag:"+ id);


        //code : 204(no content), 404(not found)
        return deleteFlag != 0
            ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() 
            : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    // @PatchMapping("/update/{id}")
    // public ResponseEntity<?> update(@PathVariable("id") Integer id,
    //                                 @RequestBody Map<String, Object> map){

    // }

    @PatchMapping("/update/{id}/{comment}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id,
                                    @PathVariable("comment") String comment){
        System.out.println(">>>>comment update controller");
        System.out.println(">>>>comment update param(id):"+ id);
        System.out.println(">>>>comment update param(comment):"+ comment);

        Map<String, Object> map = new HashMap<>();
        map.put("id",id);
        map.put("comment",comment);

        int updateFlag = commentService.update(map);

        return updateFlag != 0
            ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() 
            : ResponseEntity.status(HttpStatus.NOT_FOUND).build();


    }
}
