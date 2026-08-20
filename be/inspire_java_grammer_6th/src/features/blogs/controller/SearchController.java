package features.blogs.controller;

import java.util.List;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.domain.dto.BlogResponseDTO;
import features.blogs.service.ReactService;

public class SearchController {
    private ReactService service ;
    public SearchController() {
    }
    public SearchController(ReactService service ) {
        this.service = service ;
    }
    
    // view 에게 응답하는 역할의 메서드 
    public List<BlogResponseDTO> search(String keyword) {
        System.out.println("debug >>>> search controller search params :  "+keyword);  
        return service.search(BlogRequestDTO.builder()
                                    .keyword(keyword)
                                    .build()) ;  
    }
    
}
