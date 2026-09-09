package com.example.inspire_jpa.features.comments.domain.dto;

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
public class CommentResponseDTO {
    private Integer commentId;
    private String  comment, email ;
    private Integer blogId ;

    public static CommentResponseDTO fromEntity(CommentEntity entity){
        return CommentResponseDTO.builder()
                    .commentId(entity.getCommentId())
                    .comment(entity.getComment())
                    .email(entity.getEmail())
                    .blogId(entity.getBlog().getBlogId())
                    .build();
    }

}

