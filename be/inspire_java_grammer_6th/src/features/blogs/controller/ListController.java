package features.blogs.controller;
import features.blogs.domain.dto.BlogResponseDTO;
import features.blogs.service.ReactService;

import java.util.*;

public class ListController {
    
    private ReactService service ;

    public ListController(){

    }

    public ListController(ReactService sevice){
        this.service = sevice;
    }

    //view에게 응답하는 역할의 메서드
    public List<BlogResponseDTO> list(){
        System.out.println(">>>>list controller list()");
        return service.list();
    }
}
