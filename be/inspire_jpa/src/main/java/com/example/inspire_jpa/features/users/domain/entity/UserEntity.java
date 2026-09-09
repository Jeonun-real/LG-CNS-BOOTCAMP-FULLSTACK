package com.example.inspire_jpa.features.users.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity 
@Table(name = "SPRING_JPA_USER_TBL") 
// @Table(name = "flyway_users_tbl") 


@Builder 
@Getter 
@ToString 
@NoArgsConstructor 
@AllArgsConstructor 
public class UserEntity {
    
    @Id 
    @Column(name = "email", length = 50, nullable = false) 
    private String email;
    private String password; 
    private String name ;

    // option(role : 인가된 권한 : user, admin)
    private String role ;

    @OneToMany( mappedBy = "author",
                orphanRemoval = false ) 
    private List<BlogEntity> blogs = new ArrayList<>();

}
