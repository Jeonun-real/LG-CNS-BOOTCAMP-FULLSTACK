import features.blogs.domain.dto.BlogRequestDTO;
import features.blogs.domain.dto.BlogResponseDTO;
import features.operator.OperatorDemo;

public class OperatorApp {
    public static void main(String[] args) {
        OperatorDemo instance = new OperatorDemo();
        // instance.operator();
        // System.out.println(instance);
        
        // BlogResponseDTO reponse = instance.register("오늘도 화이팅", "앗", "tjwjddns1302@gmail.com");

        // System.out.println(reponse.getStatus());
        // System.out.println(reponse.getMessage());
        // System.out.println();

        // BlogRequestDTO request = new BlogRequestDTO(1,"오늘도무사히","앗","tjwjddns1302@gmail.com");

        // BlogResponseDTO res = instance.register(request);
        // System.out.println(res.getStatus());
        // System.out.println(res.getMessage());
        // System.out.println();

        String result = instance.ifWoodMain(2);
        System.out.println(result);
    }
}
