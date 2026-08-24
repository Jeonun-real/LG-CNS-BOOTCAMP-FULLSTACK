package features.blogs.domain.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class BlogResponseDTO implements Serializable {
    
    private int     status ;
    private String  message ;
    
    ////////////////////////////////////////
    private Integer blogId ;
    private String  title, content, email ;
    private Integer viewCnt ;

}

