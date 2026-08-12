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
    public String ifWoodMain(int number){
    
       if(number>=1 && number <=3){
            if(number==1){
                return "거짓";
            }else if(number==2){
                return "거짓";

            }else{
                 return "진실";

            }
       } else{
            return "왓";
       }
    }



}
