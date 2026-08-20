package features.blogs.factory;

import java.util.HashMap;
import java.util.Map;

import features.blogs.controller.InsertController;
import features.blogs.controller.ListController;
import features.blogs.controller.ReadController;
import features.blogs.controller.SearchController;
import features.blogs.repository.BlogReactDao;
import features.blogs.service.ReactService;
import features.blogs.service.ReactServiceImpl;
/*
singleton   pattern
factory     pattern 
사용자의 요청 endPoint 로 각 controller 객체를 바인딩해서 담을 것 : Map
*/
public class BeanFactory {

    private Map<String, Object>     map ; 
    private static BeanFactory  instance ; 
    
    // dependency injection
    private ReactService        service ; 
    private BlogReactDao            dao ; 


    private BeanFactory() {
        map = new HashMap<>();
        
        dao = new BlogReactDao();
        service = new ReactServiceImpl(dao);

        // 추후 추가되는 각 기능을 구현하는 xxxController 등록 
        map.put("list.inspire",     new ListController(service)); 
        
        // Q) ReadController 등록 
        map.put("read.inspire",     new ReadController(service)); 
        map.put("search.inspire",   new SearchController(service)); 
        map.put("insert.inspire",   new InsertController(service)); 
    }

    public static BeanFactory getInstance() {
        if(instance == null) {
            instance = new BeanFactory() ; 
        }
        return instance;
    }

    // front controller 가 호출하는 메서드 
    public Object getBean(String endPoint) {
        return map.get(endPoint) ;
    }


}

