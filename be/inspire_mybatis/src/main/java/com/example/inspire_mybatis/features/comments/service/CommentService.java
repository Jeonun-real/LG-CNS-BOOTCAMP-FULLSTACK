package com.example.inspire_mybatis.features.comments.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.inspire_mybatis.features.comments.domain.dto.CommentRequestDTO;
import com.example.inspire_mybatis.features.comments.repository.CommentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    public int insert(CommentRequestDTO request){
        System.out.println(">>>>comment insert service");
        System.out.println(">>>>service insert commentmapper" + request);

        commentMapper.save(request);
        return request.getId();       
    }

    public int delete(Integer id){
        System.out.println(">>>>comment delete service");
        System.out.println(">>>>service delete param: " + id);

        return commentMapper.delete(id);

    }

    public int update(Map<String,Object> map){
        System.out.println(">>>>comment update service");

        return commentMapper.update(map);
    }
    
}
