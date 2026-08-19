package features.blogs.facade;
import features.blogs.controller.ListController;
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
    
}
