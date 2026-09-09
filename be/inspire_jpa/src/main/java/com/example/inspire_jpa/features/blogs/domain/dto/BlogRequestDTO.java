package com.example.inspire_jpa.features.blogs.domain.dto;

import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;
import com.example.inspire_jpa.features.users.domain.entity.UserEntity;

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
public class BlogRequestDTO {
    
    private String title, content, category, email ;

    public BlogEntity toEntity(UserEntity user) {
        return BlogEntity.builder()
            .title(this.title)
            .content(this.content)
            .category(this.category)
            .author(user)
            .build();
    };
}


