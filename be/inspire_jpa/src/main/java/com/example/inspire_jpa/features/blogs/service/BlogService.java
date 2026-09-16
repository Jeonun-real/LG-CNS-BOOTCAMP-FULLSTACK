package com.example.inspire_jpa.features.blogs.service;

import com.example.inspire_jpa.features.users.repository.UserRepository;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inspire_jpa.features.blogs.domain.dto.BlogRequestDTO;
import com.example.inspire_jpa.features.blogs.domain.dto.BlogResponseDTO;
import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;
import com.example.inspire_jpa.features.blogs.repository.BlogRepository;
import com.example.inspire_jpa.features.openai.domain.RecommandResponseDTO;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;

@Service 
@RequiredArgsConstructor 
public class BlogService {
    
    //agent 시용시 주석 필요
    //ai
    // private final ChatClient chatClient;

    private final OkHttpClient okHttpClient;

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

        // email from sercurity context holder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        System.out.println("debug >>>> blog service insert params email : "+request.getEmail()); 
        System.out.println("debug >>>> blog service securitycontext holder email : "+email); 

        return userRepository.findById(email)
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
        // return blogRepository.findByComments(id)
        //             .map(BlogResponseDTO::fromEntity)
        //             .orElseThrow(() -> new RuntimeException(id+" BLOG NOT FOUND")) ;

        // blog + comments
        return blogRepository.findByComments(id)
                    .map(BlogResponseDTO::fromEntityWithComments)
                    .orElseThrow(() -> new RuntimeException(id+" BLOG NOT FOUND")) ;
    }

    // public String contentGenerate(Map<String, Object> map){
    //     String result = chatClient
    //         .prompt()
    //         .user("""
    //             넌 일본의 나츠메소세키 작가의 문체를 바타응로 블로그를 작성하는 전문가야
    //             주어진 카테고리와 키워드를 기반으로 나츠메소세키 문체의 블로그를 작성해줘
    //             글자수는 200자 이내로 작성해줘
    //             <조건>
    //                 - 카테고리 : "%s"
    //                 - 키워드 : "%s"
    //             </조건>
    //             """.formatted((String)(map.get("catergory")),
    //                           (String)(map.get("keyword"))))
    //         .call()
    //         .content();
    //     return result;

    // }

    public List<BlogResponseDTO> searchByKeyword(Map<String, Object> map){
        System.out.println(">>>> blog service searchbykeyword by blogai tools");
        return blogRepository
                    .findByContentAndCategory((String)(map.get("keyword")), 
                                              (String)(map.get("category")))
                                              .stream()
                                              .map(BlogResponseDTO::fromEntity)
                                              .toList();
    }


}


