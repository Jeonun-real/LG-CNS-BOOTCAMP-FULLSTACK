import features.blogs.domain.dto.BlogResponseDTO;
import features.operator.OperatorDemo;

public class OperatorApp {
    public static void main(String[] args) {
        OperatorDemo instance = new OperatorDemo();
        // instance.operator();
        // System.out.println(instance);
        
        BlogResponseDTO reponse = instance.register("오늘도 화이팅", "앗", "tjwjdns1302@gmail.com");

        System.out.println(reponse.getStatus());
        

    }
}
