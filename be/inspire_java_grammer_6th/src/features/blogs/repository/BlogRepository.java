package features.blogs.repository;

import features.blogs.domain.dto.BlogResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
repository, dao
- data access logic
- JDBC를 기본으로해서 object relationship mapping(mybatis, JPA) 확장
- 원격에 있는 database와 통신을 해서 crud를 잔담하는 객체
*/

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BlogRepository {
    
    private BlogResponseDTO[] list = new BlogResponseDTO[10];

    public BlogResponseDTO[] blogs(){
        System.out.println("BlogRepository blogs()");
        return list;
    }

}
