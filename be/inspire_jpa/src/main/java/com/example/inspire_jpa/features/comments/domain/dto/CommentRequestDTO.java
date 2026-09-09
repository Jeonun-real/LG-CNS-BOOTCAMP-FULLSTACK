package com.example.inspire_jpa.features.comments.domain.dto;

import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;
import com.example.inspire_jpa.features.comments.domain.entity.CommentEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    private Integer id ;
    private String  comment, email ;
    private Integer blogId ;

    public CommentEntity toEntity(BlogEntity blog){
        return CommentEntity.builder()
            .comment(this.comment)
            .email(this.email)
            .blog(blog)
            .build();
    }
}


