package com.example.inspire_jpa.features.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inspire_jpa.features.comments.domain.entity.CommentEntity;

@Repository 
public interface CommentRepository extends JpaRepository<CommentEntity,Integer> {

} 