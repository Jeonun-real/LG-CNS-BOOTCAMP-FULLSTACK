import features.blogs.domain.dto.BlogRequestDTO;

public class BlogApp {

    public static void main(String[] args) {
        // System.out.println("객체생성");
        // BlogRequestDTO request = new BlogRequestDTO();
        // BlogRequestDTO request = new BlogRequestDTO(id:1 , title : "title", content : "content", email = "email");

        System.out.println("빌더생성");

        BlogRequestDTO request = BlogRequestDTO.builder()
                                    .title("자바")
                                    .content("재밌다")
                                    .email("tjwjddns1302@gmail.com")
                                    .build();


        request.setTitle("조기퇴근 ");

        System.out.println(request.getTitle());

    }
    
}
