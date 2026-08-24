package features.blogs.controller;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.service.ReactService;

public class FileController {
    private ReactService service ;
    public FileController() {
    }
    public FileController(ReactService service ) {
        this.service = service ;
    }

    // view 에게 응답하는 역할의 메서드 
    // 여러 파라미터를 객체로 바인딩하고 데이터의 유효성을 체크하는 역할이 필요함!!!
    public boolean save(){
        System.out.println(">>>> file controller save");
        return service.saveToFile();
    }
    public boolean load(){
        System.out.println(">>>> file controller save");
        return service.loadToFile();
    }

}
