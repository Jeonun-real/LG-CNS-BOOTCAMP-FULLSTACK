package features.blogs.service;

import java.util.List;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.domain.dto.BlogResponseDTO;

public interface ReactService {


    public List<BlogResponseDTO> list();
    public BlogResponseDTO read(int blogId);

    //CRUD : insert updater delete를 조작어로 표현 -> 대부분 반환타임은 int 0, 1 
    public int insert(BlogRequestDTO request);
    public int update(BlogRequestDTO request);
    public int delete(int blogId);

    public List<BlogResponseDTO> search();
    
} 