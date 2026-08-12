import features.stat.StaticDemo;

public class StaticApp {
    public static void main(String[] args) {
        StaticDemo demo = new StaticDemo();
        System.out.println(demo.message);
        System.out.println(StaticDemo.staticMessage);


        demo.message = "메세지 변경";
        System.out.println(demo.message);

        System.out.println(StaticDemo.PI);


        demo.nonStaticMethod();
        StaticDemo.classMethod();

    }
}
