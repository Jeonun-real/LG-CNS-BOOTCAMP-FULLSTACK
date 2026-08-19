package features.blogs.domain.dto;

import lombok.*;

@Builder //객체생성
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class BlogResponseDTO {
    private int status;
    private String message;
    ///////////////////////////////////////
    private Integer blogId;
    private String title, content, email;
    private Integer viewCnt;
    
}
