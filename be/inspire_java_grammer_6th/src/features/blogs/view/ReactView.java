package features.blogs.view;

import java.util.*;

import features.blogs.facade.FrontController;
import features.blogs.util.ResponseEntity;
import features.blogs.domain.dto.BlogResponseDTO;

public class ReactView {
    /*
    Scanner 데이터 입력 처리
    예외처리
    view the a FrontController
    */

    private Scanner scan;
    private FrontController front;

    //생성자-> 멤버변수의 초기화 진행
    public ReactView(){
        scan = new Scanner(System.in);
        front = new FrontController();
    }
    
    public void landingPage(){
        boolean isLoad = front.file("file.inspire","load");
        System.out.println(isLoad? "데이터 로딩완료!!" : "데이터 로딩 등 실패");
        while(true){
            System.out.println();
            System.out.println(">>>>> inspire camp v1.0<<<<<");
            System.out.println("1. 전체검색");
            System.out.println("2. 게시글 상세보기");
            System.out.println("3. 입력화면");
            System.out.println("4. 수정화면");
            System.out.println("5. 게시글 삭제");
            System.out.println("6. 검색");
            System.out.println("99. 프로그램 종료");


            /*
            Q)
            - 사용자로부터 문자열 정수가 아닌 문자열이 입력된다면 예외발생!!
            - 예외처리를 통햇허 메시지를 출력하고 다시 입력받을 수 있도록 제어
            */
            try{
                System.out.println("원하시는 메뉴 번호를 입력하세요");
                Integer number = Integer.parseInt(scan.nextLine());
                switch (number) {
                    case 1:
                        list();
                        break;
                    case 2:
                        read();
                        break;
                    case 3:
                        insert();
                        break;
                    case 4:
                        update();
                        break;
                    case 5:
                        delete();
                        break;                        
                    case 6 : 
                        search();
                        break ;
                    case 99:
                        exit();
                        break;
                
                    default:
                        break;
                }
            }catch(Exception e){
                // System.out.println("ERR) 메뉴에 있는 숫자만 가능합니다!! 다시 확인요망");
                System.out.println();
                e.printStackTrace();
                
            }


        }

    } //landingPage block end

    public void exit() {
        System.out.println();
        System.out.println(">>>> 작업된 내용을 저장하고 시스템을 종료하시겠습니까?(y/n) ") ; 
        String yesOrNo = scan.nextLine() ;
        if( yesOrNo.equalsIgnoreCase("y")) {
            String endPoint = "file.inspire";
            System.out.println(front.file(endPoint, "save") ? "데이터 저장 완료!!" : "데이터 저장 실패");
        }
        System.exit(1);  
        
    } // exit block end 

    public void list() {
        System.out.println();
        System.out.println(">>>> 데이터 출력 ") ; 
        String endPoint = "list.inspire" ; 
        ResponseEntity<List<BlogResponseDTO>> response = front.list(endPoint) ;

        // stream api 출력 == json rendering (react)  
        if( response.getCode() == 200) {
            response.getData().stream()
            .forEach(System.out::println); 
        }
        

    }
    public void read(){
        System.out.println();
        System.out.println(">>>> 상세페이지 정보");
        String endPoint = "read.inspire";
        System.out.println("키(blogId)입력");

        int blogId = Integer.parseInt(scan.nextLine());
        BlogResponseDTO response = front.read(endPoint, blogId);
        //출력 또는 정보 없음
        System.out.println((response != null)? response : "정보없음");


    }

    public void search() {
        System.out.println();
        System.out.println(">>>> 검색페이지 정보 ") ; 
        String endPoint = "search.inspire" ; 

        /* 
        Q) blog title || content 내용 중 해당 키워드를 포함하는 것들을 검색해서 가져와 본다면?
        - front controller search() 정의
        - SearchController 클래스를 정의하고 factory 에 search.inspire 등록 
        - BlogReactServiceImpl 클래스에 search() 메서드 정의하고 dao findByKeyword() 메서드를 호출
        - BlogReactDao 클래스에 findByKeyword() 구현 
        */ 
        System.out.print("키워드 입력 : "); 
        String keyword = scan.nextLine() ;

        List<BlogResponseDTO> response = front.search(endPoint, keyword) ;
        // stream api 출력 == json rendering (react)  
        response.stream()
            .forEach(System.out::println); 
    }

    public void insert(){
        System.out.println();
        System.out.println(">>>> 데이터 입력 화면 ") ; 
        String endPoint = "insert.inspire" ; 
        
        // react data = { title : xxxxx, content : xxxxx, email : xxxxx} 
        System.out.print("제목 : "); 
        String title = scan.nextLine();
        System.out.print("내용 : "); 
        String content = scan.nextLine();
        System.out.print("이메일 : ");
        String email = scan.nextLine(); 

        // axios.post("insert.inspire" , data) ;
        int flag = front.insert(endPoint, title, content, email);
        System.out.println( (flag == 1) ? "입력성공" : "입력실패");

    }
    
    public void delete(){
        System.out.println();
        System.out.println(">>>> 데이터삭제화면");
        String endPoint = "delete.inspire";
        System.out.println("키(blogId)입력");

        int blogId = Integer.parseInt(scan.nextLine());
        int response = front.delete(endPoint, blogId);
        //출력 또는 정보 없음
        System.out.println((response == 1)? "삭제성공" : "정보없음");

    }
    
    public void update(){
        System.out.println();
        System.out.println(">>>> 데이터 수정화면");

        String endPoint = "update.inspire";
        System.out.println("수정하고자 하는 키(blogId)입력");
        int blogId = Integer.parseInt(scan.nextLine());

        System.out.print("제목 : "); 
        String title = scan.nextLine();
        System.out.print("내용 : "); 
        String content = scan.nextLine();

        int  flag = front.update(endPoint, blogId, title, content);
        System.out.println((flag == 1)? "수정성공" : "수정실패");
    }
}
