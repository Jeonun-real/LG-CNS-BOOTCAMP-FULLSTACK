import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import features.lambda.InspireFuction;

/*
java stream API
- 코드의 가독성, 병렬처리, 유지보수 향상
- 작업을 내부적으로 처리(람다식)
- 원본데이터(coleection) 손상을 가하지 않음.
- 일회성
*/
public class StreamApp {
    public static void main(String[] args) {
        InspireFuction func01 =(x,y)->x>y?x:y;
        System.out.println(func01.max(100,200));

        System.out.println();
        InspireFuction func02 =(x,y)->x+y;
        System.out.println(func02.max(100,200));

        System.out.println();
        System.out.println("supplier>>>>");
        Supplier<String> supplier = () -> "inspire";
        System.out.println(supplier.get());
        
        System.out.println();
        System.out.println("consumer>>>");
        Consumer<String> consumer 
            = (str) -> System.out.println(str.split(" ")[1]);
        consumer.accept("lgcns inspire");

        System.out.println();
        System.out.println(">>>fuction");
        Function<String, Integer> function = (str)->{
            return str.length();
        };
        int len = function.apply("lgcns inspire 6th camp(feat, jslim)");
        System.out.println(len);

        System.out.println();
        System.out.println(">>>predicate");
        Predicate<String> predicate = (str)->str.equals("lgcns");
        Boolean isFlag = predicate.test("lgcns");
        System.out.println(isFlag);

        System.out.println();
        System.out.println(">>>foreach");
        List<String> brands = Arrays.asList("samsung","lg","lgcns","inspire","camp","6th");

        brands.forEach((idx)->System.out.println(idx));
        // brands.forEach(System.out::println);

        System.out.println();
        System.out.println(">>>collection -> Stream");
        System.out.println(">>>Stream: 가공 - 연산");
        Stream<String> stream = brands.stream();

        stream.forEach((idx)->System.out.println(idx));

        System.out.println(">>>메소드 참조방식(type::메서드명)");
        stream = brands.stream();
        stream.forEach(System.out::println);


    }
}
