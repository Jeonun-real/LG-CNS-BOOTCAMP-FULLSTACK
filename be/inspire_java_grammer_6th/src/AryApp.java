import features.blogs.domain.dto.BlogResponseDTO;
import features.blogs.repository.BlogRepository;
import features.blogs.service.BlogService;

public class AryApp {
 
    /*
    array? 꾸러미
    - 참조타입
    - 단일데이터 타입만 담는다
    - 고정길이(실행시점에 re-sizing x)
    - 첨자번지에 대한 관리(0에서 시작)
    - length 라는 속성이 존재
    - []
    */
    public static void main(String[] args) {
        
        // Boolean [] ary = new Boolean[10];
        // ary[0] = true;

        // for(int i = 0; i<ary.length ; i++){
        //     System.out.println(ary[i]);
        // }

        // System.out.println();
        // System.out.println("enhanced array");
        // for(Boolean data : ary){
        //     System.out.println(data+"\t");
        // }


        /////////////////////////////////////////////////////////////
        //사용자가 frontend로 부터 글 몫록 요청이 들어 왔다면?

        BlogResponseDTO[] blogsAry = new BlogResponseDTO[10];
        BlogResponseDTO response = BlogResponseDTO.builder()
                                            .status(200)
                                            .message("good")
                                            .build();
        blogsAry[0] = response;
        blogsAry[1] = response;
        blogsAry[2] = response;

        for(int i =0;i<blogsAry.length;i++){
            BlogResponseDTO data = blogsAry[i];
            if(data==null){
                break;
            }
            System.out.println(data.getMessage());
        }

        System.out.println("main end");


        /////////////////////////////////////////
        BlogResponseDTO[] resultAry = BlogService.builder().build().blogs();
        System.out.println("main end");

    }
}
