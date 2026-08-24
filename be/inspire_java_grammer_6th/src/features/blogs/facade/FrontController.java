package features.blogs.facade;

import java.util.List;

import javax.swing.ListCellRenderer;

import features.blogs.controller.DeleteController;
import features.blogs.controller.FileController;
import features.blogs.controller.InsertController;
import features.blogs.controller.ListController;
import features.blogs.controller.ReadController;
import features.blogs.controller.SearchController;
import features.blogs.controller.UpdateController;
import features.blogs.domain.dto.BlogResponseDTO;
import features.blogs.factory.BeanFactory;
import features.blogs.util.ResponseEntity;

public class FrontController {
    
    private BeanFactory factory ; 
    public FrontController(){
        factory = BeanFactory.getInstance();
    }
    public ResponseEntity<List<BlogResponseDTO>> list(String endPoint) {
        System.out.println("debug >>>> front controller endPoint : "+endPoint); 
        Object controller = factory.getBean(endPoint);
        return ((ListController)controller).list()  ; 
    }

    public BlogResponseDTO read(String endPoint , int blogId) {
        System.out.println("debug >>>> front controller endPoint : "+endPoint+", blogId : "+blogId); 
        
        // Q) 팩토리를 통해서 ReadController 객체를 반환받고 해당 객체의 read() 메서드로 파라미터를 전달하여 반환
        Object controller = factory.getBean(endPoint);
        return ((ReadController)controller).read(blogId)  ; 
    }

    public List<BlogResponseDTO> search(String endPoint , String keyword) {
        System.out.println("debug >>>> front controller endPoint : "+endPoint+", keyword : "+keyword); 
        
        Object controller = factory.getBean(endPoint);
        return ((SearchController)controller).search(keyword)  ;  

    }

    public int insert(String endPoint , String title, String content, String email) {
        System.out.println("debug >>>> front controller endPoint : "+endPoint); 
        
        Object controller = factory.getBean(endPoint);
        return ((InsertController)controller).insert(title, content, email)  ;   

    }


    public int delete(String endPoint , int blogId) {
        System.out.println("debug >>>> front controller endPoint : "+endPoint+", blogId : "+blogId); 
        
        Object controller = factory.getBean(endPoint);
        return ((DeleteController)controller).delete(blogId)  ; 
    }

    public int update(String endPoint , int blogId, String title, String content) {
        System.out.println("debug >>>> front controller endPoint : "+endPoint); 
        
        Object controller = factory.getBean(endPoint);
        return ((UpdateController)controller).update(blogId, title, content)  ;   

    }


    public boolean file(String endPoint , String action) {
        System.out.println("debug >>>> front controller endPoint : "+endPoint+", action : "+action); 
        
        FileController controller = (FileController)factory.getBean(endPoint);
        if( action.equals("save")) {
            return controller.save() ;
        }else {
            return controller.load() ;
        }
        
    }


    




}
