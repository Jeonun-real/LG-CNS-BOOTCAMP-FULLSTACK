package features.blogs.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.domain.dto.BlogResponseDTO;

public class BlogReactDao {
    private List<BlogResponseDTO> blogs ; 
    
    public BlogReactDao(){
        blogs = new ArrayList<>(List.of(
            BlogResponseDTO.builder()
            .blogId(1).title("react").content("state").email("lim")
            .viewCnt(10).build(),
            BlogResponseDTO.builder()
            .blogId(2).title("java").content("oop").email("kim")
            .viewCnt(20).build(),
            BlogResponseDTO.builder()
            .blogId(3).title("spring").content("mybatis").email("lee")
            .viewCnt(30).build(),
            BlogResponseDTO.builder()
            .blogId(4).title("docker").content("devops").email("park")
            .viewCnt(40).build(),
            BlogResponseDTO.builder()
            .blogId(5).title("msa").content("kafka").email("lim")
            .viewCnt(50).build()
        ));
    } // constructor block end 

    public List<BlogResponseDTO> findByAll() {
        System.out.println("debug >>>> blog dao findByAll() "); 
        return blogs ; 
    }

    public Optional<BlogResponseDTO> findById(int blogId) {
        System.out.println("debug >>>> blog dao findById() params :  "+blogId );  
        
        // Q) stream 이용해서 filter 해서 찾은 객체를 반환 
        return blogs.stream()
            .filter( blog -> blog.getBlogId() == blogId )
            .findAny() ; 
    }

    public List<BlogResponseDTO> findByKeyword(BlogRequestDTO request) {
        System.out.println("debug >>>> blog dao findByKeyword params :  "+request);  

        return blogs.stream()
                    .filter( blog ->    blog.getTitle().contains(request.getKeyword()) 
                                        || blog.getContent().contains(request.getKeyword()) )
                    .toList() ; 
    }

    // 타입변경 
    public int save(BlogRequestDTO request) { 
        System.out.println("debug >>>> blog dao save params :  "+request);  
        // blogs.add(request); 
        return 0 ;
    }

    
}
