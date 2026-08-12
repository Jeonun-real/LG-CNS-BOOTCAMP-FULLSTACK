import features.stat.StaticDemo;

public class StaticApp {
    public static void main(String[] args) {
        StaticDemo demo = new StaticDemo();
        System.out.println(demo.message);
        System.out.println(StaticDemo.staticMessage);
    }
}
