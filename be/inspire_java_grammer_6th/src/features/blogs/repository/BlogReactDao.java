package features.blogs.repository;

import java.util.ArrayList;
import java.util.Comparator;
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

    //참조복사
    public void setBlogs(List<BlogResponseDTO> blogs){
        this.blogs = blogs;
    }

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
        /*
            blogId stream 이용한 generator
            blogid = stream sort blogid reversed + 1

        */ 
       int blogId = blogs.stream()
                        .map(BlogResponseDTO::getBlogId)
                        .sorted(Comparator.reverseOrder())
                        .findFirst()
                        .orElse(0)+1;
        
        BlogResponseDTO response = BlogRequestDTO.toEntity(request);
        response.setBlogId(blogId);


        blogs.add(response);
        
        return 1 ;
    }

    public int delete(int blogId){
        System.out.println("debug >>>> blog dao delete params : "+blogId);
        boolean isFlag = blogs.removeIf(blog -> blog.getBlogId()==blogId);
        return (isFlag)?1:0;
    }

    public int update(BlogRequestDTO request){
        System.out.println("debug >>>> blog dao update params : "+request);
        return blogs.stream()
                    .filter(blog -> blog.getBlogId() == request.getBlogId())
                    .findAny()
                    //@setter가 없으면 오류,
                    .map(blog->{
                        blog.setTitle(request.getTitle());
                        blog.setContent(request.getContent());
                        return 1;
                    })
                    .orElse(0);        
    }

    
}
