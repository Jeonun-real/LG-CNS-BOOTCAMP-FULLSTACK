package com.example.inspire_jpa.features.blogs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.inspire_jpa.features.blogs.domain.entity.BlogEntity;

public interface BlogRepository extends JpaRepository<BlogEntity, Integer>{
    /*  JPQL
    @Query("""
        SELECT  
        FROM    BlogEntity
        JOIN    
        WHERE   
    """)
    */
    @Query("""
        SELECT      b
        FROM        BlogEntity b
        LEFT JOIN   FETCH   b.comments  
        WHERE       b.blogId = :blogId
    """)
    public Optional<BlogEntity> findByComments(@Param("blogId") Integer blogId);
}


