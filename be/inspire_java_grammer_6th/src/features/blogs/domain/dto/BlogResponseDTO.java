package features.blogs.domain.dto;

import lombok.*;

@Builder //객체생성
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BlogResponseDTO {
    private int status;
    private String message;
    
}
