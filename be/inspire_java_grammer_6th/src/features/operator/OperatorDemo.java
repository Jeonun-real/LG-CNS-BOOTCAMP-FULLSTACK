package features.operator;

import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.domain.dto.BlogResponseDTO;

public class OperatorDemo {
    
    public OperatorDemo(){
    }

    // 반환타입 X, 매개변수 X
    public void operator() {
        System.out.println(">>> 산술연산자 : +, -, *, /, %, +=, -=, *=, /= etc..."); 
        System.out.println(">>> 증감연산자 : ++, --"); 
        System.out.println(">>> 삼항연산자 : (조건식) ? true : false"); 
        System.out.println(">>> 논리연산자 : &, |, !, &&, ||"); 
        System.out.println(">>> 관계연산자 : >, >=, <, <=, ==, !="); 
    }

    // 반환타입 O, 매개변수 O
    public BlogResponseDTO register(String title, String content, String email){
        System.out.println(">>>> 반환타입 O, 매개변수 O");
        System.out.println(">>>> 전달된 매개변수를 가지고 업무로직을 처리 ");
        if( email == "jslim9413@naver.com") {
            return new BlogResponseDTO(201, "OK");
        } else {
            return new BlogResponseDTO(400, "FAIL");
        }
    }

    public BlogResponseDTO register(BlogRequestDTO request){
        if( request.getEmail() == "jslim9413@naver.com") {
            return new BlogResponseDTO(201, "OK");
        } else {
            return new BlogResponseDTO(400, "FAIL");
        }
    }

    /* 
    Q)
    매개변수 number 값의 범위 : 1 ~ 3 
    - 1 : 금도끼, 2 : 은도끼, 3 : 쇠도끼
    - 나무꾼이 자기의 도끼가 1번이라고 하면 -> 산신령이 대답하길 "거짓말하는구나"
    - 나무꾼이 자기의 도끼가 2번이라고 하면 -> 산신령이 대답하길 "또 거짓말하는구나"
    - 나무꾼이 자기의 도끼가 3번이라고 하면 -> 산신령이 대답하길 "정직하구나 너에게 모든 도끼를 주겠다"
    */
    public String woodMan(int number) {
        // case 01.
        // if (number >= 1 && number <= 3) {
        //     if (number) {
        //         return "거짓말";
        //     } else if (number == 2) {
        //         return "또 거짓말";
        //     } else {
        //         return "정직하네";
        //     }
        // } else {
        //     return "예?";
        // }

        // case 02. 
        // 삼항연산자 중첩: 범위 밖 -> 1번 -> 2번 -> 나머지(3번) 순으로 걸러진다
        // return (number < 1 || number > 3) ? "1~3 사이의 번호만 말할 수 있느니라"
        //         : (number == 1) ? "거짓말하는구나"
        //         : (number == 2) ? "또 거짓말하는구나"
        //         : "정직하구나. 너에게 모든 도끼를 주겠다";

        // case 03 
        // switch (number) {
        //     case 1:
        //         return "산신령이 대답하길 ~ 거짓말하는구나";
        //     case 2:
        //         return "산신령이 대답하길 ~ 또 거짓말하는구나";
        //     case 3:
        //         return "산신령이 대답하길 ~ 정직하구나 너에게 모든 도끼를 주겠다";
        //     default:
        //         return "1 ~ 3 사이의 숫자를 입력해주세요.";
        // }

        // case 04
        // case params type : byte, short, int, char, String, enum 
        // lambda : -> 
        
        // return switch(number) {
        //     case 1  ->      "산신령이 대답하길 ~ 거짓말하는구나";
        //     case 2  ->      "산신령이 대답하길 ~ 또 거짓말하는구나";
        //     case 3  ->      "산신령이 대답하길 ~ 정직하구나 너에게 모든 도끼를 주겠다";
        //     default ->      "1 ~ 3 사이의 숫자를 입력해주세요.";
        // }; 

        String result = null ;
        switch(number) {
            case 1  ->      result = "산신령이 대답하길 ~ 거짓말하는구나";
            case 2  ->      result = "산신령이 대답하길 ~ 또 거짓말하는구나";
            case 3  ->      result = "산신령이 대답하길 ~ 정직하구나 너에게 모든 도끼를 주겠다";
            default ->      result = "1 ~ 3 사이의 숫자를 입력해주세요.";
        } 
        return result ;
    }

    /*
    반복구문?(for ~ , while, do ~ while) 
    - [] , Collection API(List, Set, Map) , 
    - ver 8 ~ Stream API(forEach ~) : lambda expression
    - 함수형 인터페이스(Supplier, Consumer, Function, Predicate)
    - 요소의 타입으로 기본타입보다 참조타입을 활용하는 방법 
    - continue, break : 반복도중 종료, 계속진행하기 위한 키워드 
    for(초기식 ; 조건식 ; 증감식) {
    
    }
    */

    // 하한값의 경계와 상한값의 경계를 포함하는 총합을 반환하고 싶다면?
    public int sumNumber(int start, int end) {
        int result  = 0 ;
        int temp    = 0 ;
        if(start > end) {
            temp    = start ;
            start   = end ;
            end     = temp ;
        }

        for(int data = start ; data <= end ; data++) {
            // result = result + data ;
            result += data ;
        }
        return result ;
    }

    /* 
    Q)
    ? static 
    - 1 ~ 100 사이의 난수를 발생시킬예정(어떻게?)
    - casting
    int nan = (int)(Math.random() * 100) + 1 ;
    - 1 ~ 해당 난수까지의 누적합을 계산
    hint)
    - static 
    - arugument X, 
    - return type : int 
    - method name : sumRandom 
    */
    
    public static int sumRandom() {
        int result = 0 ;
        int nan = (int)(Math.random() * 100) + 1 ;
        System.out.println("debug >>>> generate nan = "+nan); 
        
        // for
        // for(int data = 1 ; data <= nan ; data++) {
        //     result += data ;
        // }

        // while
        // int data = 1 ; 
        // while(data <= nan) {
        //     result += data ;
        //     data++ ;
        // }

        // do ~ while() 
        int data = 1 ; 
        do {
            result += data ;
            data++ ;
        }while(data <= nan);

        return result ;
    }

    /*
    format : %d, %s, %f
    System.out.printf()

    - argument : int, 
    - return type : void 
    - method name : printGugudan
    */

    public void printGugudan(int dan) {
        for(int idx = 1 ; idx <= 9 ; idx++) {
            System.out.printf("%d * %d = %d\t" , dan, idx, (dan * idx));
        }
    }

    /*
    Q) 
    - 5단까지만 출력하고 루프를 빠져나가고 싶다면?
    */
    public void gugudan() {
        outer:
        for(int row=2 ; row <= 9; row++) {
            // if(row == 5) {
            //     continue ; 
            // }
            inter:
            for(int col=1; col <= 9; col++) {
                // if( row == 5) {
                //     break outer ;
                // }
                System.out.printf("%d * %d = %d\t" , row, col, (row*col));
            }
            System.out.println();
            
        }
    }

    // Q) 문자열도 반복구문 이용이 가능할까?
    public void popStr(String str) {
        System.out.println("debug >>>> params : "+str); 
        System.out.println("debug >>>> str length : "+str.length());  
        // System.out.println( str.charAt(1) ) ;
        for(int idx = str.length()-1 ; idx >= 0 ; idx--) {
            System.out.print(str.charAt(idx)); 
        }
        System.out.println();

    }



}

