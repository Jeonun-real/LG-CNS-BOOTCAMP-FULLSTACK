package com.example.inspire_mybatis.features.blogs.repository;

import java.nio.file.OpenOption;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.example.inspire_mybatis.features.blogs.domain.dto.BlogRequestDTO;
import com.example.inspire_mybatis.features.blogs.domain.dto.BlogResponseDTO;

@Mapper
public interface BlogMapper {    
    public List<BlogResponseDTO> findByAll();
    public int                   save(BlogRequestDTO request);
    public Optional<BlogResponseDTO> findById(Integer id);

}
