package features.blogs.controller;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.service.ReactService;

public class UpdateController {

    private ReactService service ;

    public UpdateController() {
    }

    public UpdateController(ReactService service ) {
        this.service = service ;
    }

    // view 에게 응답하는 역할의 메서드 
    // 여러 파라미터를 객체로 바인딩하고 데이터의 유효성을 체크하는 역할이 필요함!!!
    public int update(int blogId, String title, String content) {
        System.out.println("debug >>>> insert controller update params : "+title+"\t"+content+"\t"+blogId); 
        
        return service.update(BlogRequestDTO.builder()
                                            .title(title)
                                            .content(content)
                                            .blogId(blogId).build()) ;  
                                            
    }
}
