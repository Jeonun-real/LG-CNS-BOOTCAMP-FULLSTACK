package features.oop.tv;

/*
singleton - 인스턴스를 하나로 유지하는 방법
- 생성자의 접근제어자를 private로 잡고
- static(class 소유)으로 자기자신을 선언
- static method 자신의 인스턴스를 생성하고 반환
*/
public class SamsungTV implements TV{
    
    private static SamsungTV instance;

    private SamsungTV(){

    }

    public static SamsungTV getInstance(){
        if(instance == null){
            instance = new SamsungTV();
        }
        return instance;
    }

    @Override
    public void turnOn() {
        System.out.println("samsung tv power on");
    }
}
