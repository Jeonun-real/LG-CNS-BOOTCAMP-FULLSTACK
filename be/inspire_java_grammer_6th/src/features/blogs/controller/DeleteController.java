package features.blogs.controller;

import features.blogs.service.ReactService;

public class DeleteController {
    private ReactService service ;
    
    public DeleteController() {

    }
    public DeleteController(ReactService service ) {
        this.service = service ;
    }

    public int delete(int blogId){
        System.out.println("debug >>>> delete controller delete params :  "+blogId ); 
        // Q) service delete() 호출해서 반환 
        return service.delete(blogId) ;

    }

}
