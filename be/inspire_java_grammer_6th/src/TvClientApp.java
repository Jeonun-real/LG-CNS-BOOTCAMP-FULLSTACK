import features.oop.factory.BeanFactory;
import features.oop.tv.LgTV;
import features.oop.tv.SamsungTV;
import features.oop.tv.TV;

public class TvClientApp {
    
    public static void main(String[] args) {
        // TV tv = SamsungTV.getInstance();
        // System.out.println("address"+tv);
        // tv.turnOn();

        // TV tv1 = SamsungTV.getInstance();
        // System.out.println("address"+tv1);
        // tv.turnOn();

        BeanFactory factory = BeanFactory.getInstance();
        TV tv = factory.getBrand("lg");
        System.out.println();
        TV tv1 = factory.getBrand("samsung");
        System.out.println();

        tv.turnOn();
        tv1.turnOn();
    }
}
