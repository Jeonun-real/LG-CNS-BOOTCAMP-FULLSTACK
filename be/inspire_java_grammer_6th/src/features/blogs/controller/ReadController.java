package features.blogs.controller;

import features.blogs.domain.dto.BlogResponseDTO;
import features.blogs.service.ReactService;

public class ReadController {
    /*
    blog service와의 의존성 형성
     */
    private ReactService service ;
    public ReadController() {
    }
    public ReadController(ReactService service ) {
        this.service = service ;
    }

    public BlogResponseDTO read(int blogId) {
        System.out.println("debug >>>> read controller read params :  "+blogId ); 
        // Q) service read() 호출해서 반환 
        return service.read(blogId) ;
    }
}
