package com.example.inspire_mybatis.features.blogs.domain.dto;

import java.util.List;

import com.example.inspire_mybatis.features.comments.domain.dto.CommentResponseDTO;

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
    private Integer id;
    private String title, content,category,email;

    //blog(1) : comment(N)
    private List<CommentResponseDTO> comments;

}
