package features.blogs.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.domain.dto.BlogResponseDTO;
import features.blogs.repository.BlogReactDao;

public class ReactServiceImpl implements ReactService {


    private BlogReactDao dao;
    public ReactServiceImpl(){

    }
    public ReactServiceImpl(BlogReactDao dao){
        this.dao = dao;
    }

    @Override
    public List<BlogResponseDTO> list() {
        System.out.println(">>>>blog service list");
        return dao.findByAll();
    }

    @Override
    public BlogResponseDTO read(int blogId) {
        System.out.println(">>>>blog service read param: "+blogId);
        //dao에 findbyid 호출을 통해 전달된 Optional 을 확인하고 객체 또는 null을 반환하는 구현

        return dao.findById(blogId).orElse(null); 
    }

    @Override
    public int insert(BlogRequestDTO request) {
        System.out.println("debug >>>> blog service insert params :  "+request); 
        return dao.save(request) ;    
    }

    @Override
    public int update(BlogRequestDTO request) {
        System.out.println("debug >>>> blog service update params :  "+request);
        return dao.update(request);  
    }

   
    @Override
    public int delete(int blogId) {
        System.out.println(">>>>blog service delete param: "+blogId);
        return dao.delete(blogId);

    }

    @Override
    public List<BlogResponseDTO> search(BlogRequestDTO request) {
        System.out.println("debug >>>> blog service search params :  "+request);
        return dao.findByKeyword(request);  
    }
    
    @Override
    public boolean saveToFile() {
        System.out.println(">>>>blog service file save:");
        String path = "./be/inspire_java_grammer_6th/blogs.txt";
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)))){
            oos.writeObject(dao.findByAll());
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean loadToFile() {
        System.out.println(">>>>blog service file load:");
        String path = "./be/inspire_java_grammer_6th/blogs.txt";
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)))){
            dao.setBlogs((List<BlogResponseDTO>)ois.readObject());
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
