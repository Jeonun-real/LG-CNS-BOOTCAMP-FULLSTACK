package features.blogs.controller;
import features.blogs.domain.dto.BlogResponseDTO;
import features.blogs.service.ReactService;
import features.blogs.util.ResponseEntity;

import java.util.*;

public class ListController {
    
    private ReactService service ;

    public ListController(){

    }

    public ListController(ReactService sevice){
        this.service = sevice;
    }

    // view 에게 응답하는 역할의 메서드 
    public ResponseEntity<List<BlogResponseDTO>> list() {
        System.out.println("debug >>>> list controller list() "); 
        return new ResponseEntity<List<BlogResponseDTO>>(200, "ok", service.list()) ; 
    }
    
}   
