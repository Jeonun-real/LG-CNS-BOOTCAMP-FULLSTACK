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

    /////////////////////////////
    private String  keyword ; 

    // JPA 기반에서는 xxxRequestDTO -> xxxxEntity 변경되어야하는데 이 때 사용하는 패턴 : 정적 메서드 패턴 
    // xxxxEntity -> xxxxResponseDTO
    
    public static BlogResponseDTO toEntity(BlogRequestDTO request) {
        return BlogResponseDTO.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .email(request.getEmail())
                    .build() ;
    }     
}
