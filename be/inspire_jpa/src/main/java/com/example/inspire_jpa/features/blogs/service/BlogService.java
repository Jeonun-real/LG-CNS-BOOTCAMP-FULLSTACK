package com.example.inspire_jpa.features.blogs.service;

import com.example.inspire_jpa.features.users.repository.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inspire_jpa.features.blogs.domain.dto.BlogRequestDTO;
import com.example.inspire_jpa.features.blogs.domain.dto.BlogResponseDTO;
import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;
import com.example.inspire_jpa.features.blogs.repository.BlogRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class BlogService {
    
    private final UserRepository    userRepository;
    private final BlogRepository    blogRepository ;

    @Transactional(readOnly = true)
    public List<BlogResponseDTO> list() {
        System.out.println("debug >>>> blog service list "); 
        System.out.println("debug >>>> blog service list blogMapper : "+blogRepository); 
        return blogRepository.findAll()
                    .stream()
                    .map(BlogResponseDTO::fromEntity)
                    .toList() ;

    }

    public BlogResponseDTO insert(BlogRequestDTO request) {
        System.out.println("debug >>>> blog service insert "); 
        System.out.println("debug >>>> blog service insert params : "+request); 
        /* 
        1. request.getEmail() -> select 
        2. BlogEntity( toEntity(UserEntity(email)) )
        3. save() 
        */

        return userRepository.findById(request.getEmail())
                    .map(user -> {
                        BlogEntity blog = blogRepository.save(
                            request.toEntity(user)
                        ) ;

                        return BlogResponseDTO.fromEntity(blog) ;
                    })
                    .orElseThrow(() -> new RuntimeException("Blog Insert Fail!!")) ;
    }


    /*
    bad case)
    blog 1 : comments 100 
    BlogEntity entity = blogRepository.findById(blogId).get() 
    List<CommentEnttiy> comments = blog.getComments();
    entity.setComments(comments) 

    select * from blogs where id = ? ; 
    select * from comments where blog_id = ? ; 

    godd case) 
    blog + comments 한번에 조회 
    */
    @Transactional(readOnly = true)
    public BlogResponseDTO read(Integer id) {
        // blog {} select - comment [] select  
        System.out.println("debug >>>> blog service read "); 
        System.out.println("debug >>>> blog service read params : "+id); 
        
        // case 01
        // BlogResponseDTO blog = blogMapper
        //         .findById(id)
        //         .orElseThrow(() -> new RuntimeException(id+" BLOG NOT FOUND")) ;
        

        // bad case 
        // blog.setComments(commentMapper.findByBlogId(blog.getId())); 

        
        // return blog.toBuilder()
        //         .comments(commentMapper.findByBlogId(blog.getId()))
        //         .build() ; 

        // case 02 
        // BlogResponseDTO blog = blogMapper
        //         .findById(id)
        //         .map( dto -> BlogResponseDTO.builder()
        //                         .id(dto.getId())
        //                         .title(dto.getTitle())
        //                         .content(dto.getContent())
        //                         .comments(commentMapper.findByBlogId(dto.getId()))
        //                         .build())
        //         .orElseThrow(() -> new RuntimeException(id+" BLOG NOT FOUND")) ;

        // 단순 blog
        return blogRepository.findByComments(id)
                    .map(BlogResponseDTO::fromEntity)
                    .orElseThrow(() -> new RuntimeException(id+" BLOG NOT FOUND")) ;

        // blog + comments
        // return blogRepository.findByComments(id)
        //             .map(BlogResponseDTO::fromEntityWithComments)
        //             .orElseThrow(() -> new RuntimeException(id+" BLOG NOT FOUND")) ;
    }




}


