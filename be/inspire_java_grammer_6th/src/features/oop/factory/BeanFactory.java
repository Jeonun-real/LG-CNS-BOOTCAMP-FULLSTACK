package features.oop.factory;

import features.oop.tv.LgTV;
import features.oop.tv.SamsungTV;
import features.oop.tv.TV;

    public class BeanFactory {
        
    private static BeanFactory instance;
    private TV [] ary ;

    private BeanFactory(){
        ary = new TV[2];
        ary[0] = SamsungTV.getInstance();
        ary[1] = LgTV.getInstance();
    }

    public static BeanFactory getInstance(){
        if(instance == null){
            instance = new BeanFactory();
        }
        return instance;
    }

    public TV getBrand(String brandName){
        return brandName.equalsIgnoreCase("samsung")? ary[0] : ary[1];
    }
}
