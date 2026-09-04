package com.example.inspire_mybatis.features.comments.domain.dto;

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
    private Integer id;
    private String comment, email;
    private Integer blogId;
}
