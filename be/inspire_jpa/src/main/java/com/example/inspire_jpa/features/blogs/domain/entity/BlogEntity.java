package com.example.inspire_jpa.features.blogs.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.inspire_jpa.features.comments.domain.entity.CommentEntity;
import com.example.inspire_jpa.features.users.domain.entity.UserEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;



@Entity 
@Table(name = "SPRING_JPA_BLOG_TBL") 
// @Table(name = "flyway_blogs_tbl") 


@Builder 
@Getter 
@ToString 

@NoArgsConstructor 
@AllArgsConstructor 

public class BlogEntity {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment  
    private Integer blogId ;

    private String title ;

    private String content ;

    @Column(columnDefinition = "VARCHAR(20) DEFAULT '전체' CHECK( CATEGORY IN ('전체', '개발', '생활', '취미', '일상'))")
    private String category ;


    // foreign key 
    // fetch : select * from userEntity where email = ? 
    // optional = false : null 허용하지 않음.
    @ManyToOne(fetch = FetchType.LAZY , optional = false )
    @JoinColumn(name = "email") 
    private UserEntity author ; 

    // user(1) : blog(n) - blog(1) : comment(n) 
    // 댓글 Comments
    @OneToMany(mappedBy = "blog", orphanRemoval = false ) 
    private List<CommentEntity> comments = new ArrayList<>();
    
}
