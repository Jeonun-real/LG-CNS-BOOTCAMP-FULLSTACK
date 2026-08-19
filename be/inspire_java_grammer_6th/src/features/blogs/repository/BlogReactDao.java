package features.blogs.repository;

import java.util.ArrayList;
import java.util.List;

import features.blogs.domain.dto.BlogResponseDTO;

public class BlogReactDao {
    private List<BlogResponseDTO> blogs ;

    public BlogReactDao(){
        blogs = new ArrayList<BlogResponseDTO>(List.of(
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
    }

    public List<BlogResponseDTO> findByAll(){
        System.out.println(">>>>blog dao findbyall");
        return blogs;
    }
    
}
