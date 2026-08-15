package features.oop.tv;

public class LgTV implements TV {

    private static LgTV instance;

    private LgTV(){

    }

    public static LgTV getInstance(){
        if(instance == null){
            instance = new LgTV();
        }
        return instance;
    }
    
    @Override
    public void turnOn() {
        System.out.println("lg tv power on");
    }

}
