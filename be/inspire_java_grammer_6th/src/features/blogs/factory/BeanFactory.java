package features.blogs.factory;

import java.util.HashMap;
import java.util.Map;

import features.blogs.controller.ListController;
import features.blogs.repository.BlogReactDao;
import features.blogs.service.ReactService;
import features.blogs.service.ReactServiceImpl;

/*
singleton pattern
factory pattern
사용자의 요청 endpoint로 controller를 바인딩해서 담는 것 : Map
*/
public class BeanFactory{


    //자기자신을 객체로 생성하여
    private Map<String,Object> map;
    private static BeanFactory instance;

    //dependency injection
    private ReactService service;
    private BlogReactDao dao;

    //생성자를 private로 아무나 접근못하게 막고
    private BeanFactory(){
        map  = new HashMap<>();
        dao = new BlogReactDao();
        service = new ReactServiceImpl(dao);

        // 추후 추가되는 각 기능을 구현하는 controller를 등록
        map.put("list.inspire",new ListController(service));




    }
    
    //자신의 객체가 생성이 안되어 있을떄만 한번 생성
    public static BeanFactory getInstance(){
        if(instance==null){
            instance = new BeanFactory();
        }
        return instance;
    }

    //front controller가 호춣하는 메서드
    public Object getBean(String endPoint){
        return map.get(endPoint);
    }
    
}