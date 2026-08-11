public class VariableApp {
    


    public static void main(String args[]){
        
        //선언위치가 메서드 블록에 있으므로 지역변수
        String name = "서정운" ;
        int age = 20;
        double height = 178.0;
        char gender = 'm'; 
        boolean isMarriage = true;
        
        System.out.println("name \t"+name);
        System.out.println("age \t" + age);
        System.out.println("height \t" + height);
        System.out.println("gender \t" + gender);
        System.out.println("isMarriage \t" + isMarriage);

        //Wrapper class - boxing, unboxing 
        //primitive type <=> reference type 

        Integer ii = 10; //주소번지
        System.out.println(10+ii); //주소번지 계산되네? 이게 박싱 언박싱

        //casting
        //byte->short->int->long->float->double
        //char -> int
        //(byte) interger

        byte x =10, y=10, sum =0;
        sum = (byte)(x+y);
        System.out.println(sum);


    }
}
