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
    
    // public BlogResponseDTO register(BlogRequestDTO request){

    // }



}
