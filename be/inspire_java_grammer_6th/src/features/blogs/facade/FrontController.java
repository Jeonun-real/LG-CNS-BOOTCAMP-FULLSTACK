package features.blogs.facade;
import features.blogs.controller.InsertController;
import features.blogs.controller.ListController;
import features.blogs.controller.ReadController;
import features.blogs.controller.SearchController;
import features.blogs.domain.dto.BlogResponseDTO;
import features.blogs.factory.BeanFactory;

import java.util.*;

public class FrontController {

    private BeanFactory factory;
    
    public FrontController(){
        factory = BeanFactory.getInstance();
    }

    public List<BlogResponseDTO> list(String endPoint){
        System.out.println(">>>>front controller: "+ endPoint);
        Object controller = factory.getBean(endPoint);
        return ((ListController)controller).list();
    }

    public BlogResponseDTO read(String endpoint, int blogId){
        System.out.println(">>>>front controloer endpoint : "+endpoint);
        // 펙토리를 통해 Readcontroller를 반환받고 해당 객체의 read() 메서드를 파라미터를 전달하여 반환\
        Object controller = factory.getBean(endpoint);
        return ((ReadController)controller).read(blogId);

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


    
}
