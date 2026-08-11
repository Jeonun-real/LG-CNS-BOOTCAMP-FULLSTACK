package features.blogs.domain.dto;

import lombok.*;

@Builder //객체생성
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BlogRequestDTO {
    
    private int id;
    private String title;
    private String content;
    private String email;

    
}
