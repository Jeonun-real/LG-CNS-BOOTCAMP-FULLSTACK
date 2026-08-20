package features.blogs.service;

import java.util.List;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.domain.dto.BlogResponseDTO;

public interface ReactService {

    public List<BlogResponseDTO>    list(); 
    public BlogResponseDTO          read(int blogId) ;  
    
    // CURD : insert, update, delete
    public int                      insert(BlogRequestDTO request) ;
    public int                      update(BlogRequestDTO request) ; 
    public int                      delete(int blogId) ;

    public List<BlogResponseDTO>    search(BlogRequestDTO request); 

}
 
