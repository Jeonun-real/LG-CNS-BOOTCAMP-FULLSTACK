package features.exception;

import features.exception.util.InspireException;

/*
Error : 논리적 오류, etc...
Exceotion: mild error
예외(예기치 못한 상황) 처리방법
- 예외발생 시점
    - compile(IOException 상속)
    - runtime(RuntimeException 상속)

예외처리방법
- case01
try{
    예외코드
}catch(발생된 예외객체를 정정의){
    예외를 처리하고 예외발생시 수행하는 블럭
}finally{
    예외발생여부와 상관없이 무조건 수행되는 블럭
}

-case02
public void xxxxx() threows xxxxException{
    예외코드
}

사용자 정의 예외클랙스
public class xxxx extends Exception {

}

강제로 예외를 발샐시키는 상황
thtrow new XXXXException();


*/
public class ExceptionDemo  {
    public void first(int x) throws InspireException{
        System.out.println("first method start");
        try{
            if(x<0){
                throw new InspireException("양의 정수만 가능합니다");
            }
        }finally{
            System.out.println("first method end");
        }
    }
} 