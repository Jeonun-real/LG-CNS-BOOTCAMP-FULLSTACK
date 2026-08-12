package features.stat;

public class StaticDemo {
    /*
        static(클래스, 변수, 메서드)
        - static : 선언된 변수, 메서드는 인스턴스 소유가 아닌 클래스 소유
        final: 상수(const)
        - static final int PI =3.14
    */

    public String message = "인스턴스";
    public static String staticMessage = "클래스 소유";
    public static final double PI = 3.14;

    public StaticDemo(){


    }

    public void nonStaticMethod(){
        System.out.println(message);
        System.out.println(staticMessage);
        System.out.println(PI);

    }

    public static void classMethod(){
        //error
        //static 소유는 instance 소유(변수, 메서드)에 접근불가
        //반드시 인스턴스 만들고 접근할 것
        // System.out.println(message);
        // System.out.println(new StaticDemo().message);


        System.out.println(staticMessage);
        System.out.println(PI);
    }

}
