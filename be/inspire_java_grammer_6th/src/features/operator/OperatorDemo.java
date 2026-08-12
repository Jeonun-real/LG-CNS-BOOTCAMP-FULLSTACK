package features.operator;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.domain.dto.BlogResponseDTO;

public class OperatorDemo {
    
    public OperatorDemo(){

    }

    //반환타입 x , 매개변수x
    public void operator(){
        System.out.println("산술연산자 : +, -, *, /, %, +=, -=, *=, /=, etc...");
        System.out.println("증감연산자 : ++, --");
        System.out.println("삼항연산자 : (조건식) ? true, false");
        System.out.println("논리연산자 : &, |, !, &&, ||");
        System.out.println("관계연산자 : <, >, <=, >=, ==, !=");
    }

    
    public BlogResponseDTO register(String title, String content, String email){
        System.out.println("반환타입o, 매개변수o");
        System.out.println("전달된 매개변수를 가지고 업무로직을 처리");

        if(email == "tjwjddns1302@gmail.com"){
            return new BlogResponseDTO(201, "OK");
        } else{
            return new BlogResponseDTO(400, "FAIL");
        }


    }
    
    public BlogResponseDTO register(BlogRequestDTO request){

        if(request.getEmail() == "tjwjddns1302@gmail.com"){
            return new BlogResponseDTO(201, "OK");
        } else{
            return new BlogResponseDTO(400, "FAIL");
        }
    }

    /* q) 
    매개변수 number 값의 범위 1~3
    1, 금 2. 은 3. 쇠
    - 나무꾼이 자기의 도끼가 1번이라 하면 -> 산신령이 거짓말이라고 함
    - 나무꾼이 자기의 도끼가 1번이라 하면 -> 산신령이 거짓말이라고 함
    - 나무꾼이 자기의 도끼가 1번이라 하면 -> 산신령이 정직하다고 도끼  다 주겠다 함
     */
    // public String ifWoodMain(int number){
    
    //    if(number>=1 && number <=3){
    //         if(number==1){
    //             return "거짓";
    //         }else if(number==2){
    //             return "거짓";

    //         }else{
    //              return "진실";

    //         }
    //    } else{
    //         return "왓";
    //    }


    //     return (number <1 || number>3) ? "1~3사이만 가능"
    //         :(number==1)? "거짓말"
    //         :(number==2)? "거짓말"
    //         : "정직";

    //     switch (number) {
    //         case 1:
    //             return "거짓";
    //         case 2:
    //             return "거짓";
    //         case 3:
    //             return "진실";           
    //         default:
    //             return "미침";
    //     }

    //     return switch (number) {
    //         case 1 -> "거짓";
    //         case 2 -> "거짓";
    //         case 3 -> "진실";
    //         default -> "거짓";
    //     };
    //     String result = null;
    //     switch (number) {
    //         case 1 -> result = "거짓";
    //         case 2 -> result ="거짓";
    //         case 3 -> result ="진실";
    //         default -> result ="거짓";
    //     };
    //     return result;


    // }

        /*
        반복구문(for, while, do~while)
        - [], collection API(List, Set, Map), 
        - var 8 ~ Stream API(forEach ~) : lambda expression
        - 함수형 인터페이스(supplier, Consumer, Fuction, Prediction) 
        - 요소의 타입으로 기본타입보다 참조타입을 활용하는 방법
        - continue, break : 반복도중 종료, 계속진행 
       for(초기식;조건식;증감식){
       
       }
        */


        //하한값의 경게와 상한값의 경계를 포함하는 총합을 반환하고 싶다면?
        // public int sumNumber(int start, int end){
        //     int result = 0;
        //     int temp = 0;
        //     //start, end 바꾸기
        //     if(start>end){
        //         temp = start;
        //          start= end;
        //          end = temp;
        //     }
        //     for(int i = start ; i<=end;i++){
        //         result += i;
        //     }
        //     return result ;
        // }

        /*
        ? static
        - 1 ~ 100사이의 난수 발생
`       Math 클래스가 가진 난수 호출

        casting
        int num = (int)(Math.random()*100)+1

        1 ~ 해당 난수까지의 누적합을 계산
        - static
        - argument x
        - return type: int
        - method name: sumRandom
        */

        public static int sumRandom(){
            int result = 0;

            // logic
            int num = (int)(Math.random()*100)+1;
            System.out.println("generate nan=" + num);

            //for
            // for(int i = 1 ; i<=num; i++){
            //     result +=i;
            // }

            //while 초기식, 조건식, 증감식
            // int i = 1 ;
            // while(i<=num){
            //     result +=i;
            //     i++;
            // }

            // return result;

            //do ~ while()
            int i = 1;
            do{
                result +=i;
                i++;

            }while(i<=num);

            return result;

            /*
            format : %d %s %f
            - argument : int
            - return type: void
            - method name: printGugudan
            */
        }

        public void printGugudan(int dan){
            for(int idx = 1 ; idx<=9 ; idx ++){
                System.out.printf("%d*%d=%d\t", dan, idx, (dan*idx));
            }
        }

        public void gugudan(){
            outer:
            for(int row=2;row<=9;row++){
                inner:
                for(int col=1;col<=9;col++){
                    System.out.printf("%d*%d=%d\t", row, col, row*col);
                }
                System.out.println();

            }
        }

        //문자열 반복
        public void popstr(String str){
            System.out.println("params: "+str);
            System.out.println("length: "+str.length());
            System.out.println(str.charAt(1));
            for(int idx = str.length()-1 ; idx>0;idx--){
                System.out.print(str.charAt(idx));
            }
            System.out.println();

        
        }
        




}
