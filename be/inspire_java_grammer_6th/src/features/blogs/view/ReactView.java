package features.blogs.view;

import java.util.*;

import features.blogs.facade.FrontController;

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
                    case 99:
                        exit();
                        break;
                
                    default:
                        break;
                }
            }catch(Exception e){
                System.out.println("ERR) 메뉴에 있는 숫자만 가능합니다!! 다시 확인요망");
            }


        }

    } //landingPage block end

    public void exit(){
        System.out.println();
        System.out.println(">>>시스템을 종료합니다<<<");
        System.exit(1);
    }

    public void list(){
        System.out.println();
        System.out.println(">>>> 데이터 출력");
        String endPoint = "list.inspire";
        List<BlogResponseDTO> response = front.list(endPoint);

        //stream api 출력 = json의 데이터를 렌더링
        response.stream()
            // .forEach(System.out::println);
            .forEach(blog->System.out.println(blog));
    }

}
