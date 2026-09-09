package com.example.inspire_jpa.features.blogs.domain.dto;

import java.util.List;

import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;
import com.example.inspire_jpa.features.comments.domain.dto.CommentResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDTO {

    private Integer blogId ;
    private String  title, content, category, email ;

    //1-N
    private List<CommentResponseDTO> comments;

    // blogs 반환하는 단순구조
    public static BlogResponseDTO fromEntity(BlogEntity entity) { 
        return BlogResponseDTO.builder()
                    .blogId(entity.getBlogId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .category(entity.getCategory())
                    .email(entity.getAuthor().getEmail())
                    .build();
    }

    // blog(1) + comments(N) 반환도 필요함!!!
    public static BlogResponseDTO fromEntityWithComments(BlogEntity entity) { 
        return BlogResponseDTO.builder()
                    .blogId(entity.getBlogId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .category(entity.getCategory())
                    .email(entity.getAuthor().getEmail())
                    .comments(
                        entity.getComments()
                            .stream()
                            .map(CommentResponseDTO::fromEntity)
                            .toList()
                    )
                    .build();
    }

}





