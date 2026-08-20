package features.blogs.service;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public int update(BlogRequestDTO request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updata'");
    }

    @Override
    public int delete(int blogId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<BlogResponseDTO> search() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }
    
}
