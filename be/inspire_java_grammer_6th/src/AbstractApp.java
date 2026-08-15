import features.oop.abstraction.Animal;
import features.oop.abstraction.SuperMan;
import features.oop.abstraction.Flyer;

public class AbstractApp {
    
    public static void main(String[] args) {
        // Animal animal = new SuperMan();
        // Animal animal = new Animal();

        // Animal [] ary = new Animal[10];

        Flyer superman = new SuperMan();
        ((SuperMan)superman).eating("빵");
    }
}
