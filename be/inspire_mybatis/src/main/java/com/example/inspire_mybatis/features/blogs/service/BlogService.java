package com.example.inspire_mybatis.features.blogs.service;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inspire_mybatis.features.blogs.domain.dto.BlogRequestDTO;
import com.example.inspire_mybatis.features.blogs.domain.dto.BlogResponseDTO;
import com.example.inspire_mybatis.features.blogs.repository.BlogMapper;
import com.example.inspire_mybatis.features.comments.repository.CommentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogMapper blogMapper;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<BlogResponseDTO> list(){
        System.out.println(">>>>servicelist");
        System.out.println(">>>>servicelist blogmapper" + blogMapper);

        return blogMapper.findByAll();
    }

    public int insert(BlogRequestDTO request){
        System.out.println(">>>>blog insert service");
        System.out.println(">>>>service insert blogmapper" + blogMapper);
        return blogMapper.save(request);
    }

    @Transactional(readOnly = true)
    public BlogResponseDTO read(Integer id){
        //blog {} select - comment [] select
        System.out.println(">>>>blog read service");
        System.out.println(">>>>service read blogmapper" + id);

        BlogResponseDTO blog = blogMapper
                    .findById(id)
                    .orElseThrow(()->new RuntimeException(id+"BLOG NOT FOUND"));

        //badcase - not recommend
        // blog.setComments(commentMapper.findByBlogId(blog.getId()));

        //case 01
        return blog.toBuilder()
                        .comments(commentMapper.findByBlogId(blog.getId()))
                        .build();
        
        //case 02
        // BlogResponseDTO blog = blogMapper
        //     .findById(id)
        //     .map(dto->BlogResponseDTO.builder()
        //                                 .id(dto.getId())
        //                                 .title(dto.getTitle())
        //                                 .content(dto.getContent())
        //                                 .comments(commentMapper.findByBlogId(dto.getId()))
        //                                 .build())
        //     .orElseThrow(()->new RuntimeException(id+"BLOG NOT FOUND"));
    }

}
