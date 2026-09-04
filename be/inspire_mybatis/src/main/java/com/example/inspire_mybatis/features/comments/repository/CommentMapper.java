package com.example.inspire_mybatis.features.comments.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.inspire_mybatis.features.comments.domain.dto.CommentRequestDTO;
import com.example.inspire_mybatis.features.comments.domain.dto.CommentResponseDTO;

@Mapper
public interface CommentMapper {

    public List<CommentResponseDTO> findByBlogId(Integer blogId);

    public int save(CommentRequestDTO request);

    public int delete(Integer id);

    public int update(Map<String,Object> map);

    
}
