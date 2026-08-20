import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import features.blogs.domain.dto.BlogResponseDTO;

public class BlogStreamAPP {


    public static void main(String[] args) {
        ArrayList<BlogResponseDTO> blogs = new ArrayList<>(List.of(
            BlogResponseDTO.builder()
            .blogId(1).title("react").content("state").email("lim")
            .viewCnt(10).build(),
            BlogResponseDTO.builder()
            .blogId(2).title("java").content("oop").email("kim")
            .viewCnt(20).build(),
            BlogResponseDTO.builder()
            .blogId(3).title("spring").content("mybatis").email("lee")
            .viewCnt(30).build(),
            BlogResponseDTO.builder()
            .blogId(4).title("docker").content("devops").email("park")
            .viewCnt(40).build(),
            BlogResponseDTO.builder()
            .blogId(5).title("msa").content("kafka").email("lim")
            .viewCnt(50).build()
        ));

        System.out.println();
        System.out.println(">>>>filter : 조건검색(조회수가 30이상인 데이터만 추출");
        blogs.stream()
            .filter(blog->blog.getViewCnt()>=30)
            // .map(BlogResponseDTO::getEmail)
            .map(blog->blog.getEmail())
            .forEach(System.out::println);
        
        //이메일 계정이 lim인 사용자만 추출해서 list로 반환
        System.out.println();
        System.out.println(">>>타입변환 유도");
        List<BlogResponseDTO> result = blogs.stream()
                                                .filter(blog->blog.getEmail().equals("lim"))
                                                // .collect(Collectors.toList());
                                                .toList();
        result.forEach(System.out::println);

        //작성자별 그룹
        System.out.println();
        System.out.println(">>>Collectores.gropingby: 작성자별 그룹");
        Map<String, List<BlogResponseDTO>> map = blogs.stream()
            .collect(Collectors.groupingBy(BlogResponseDTO::getEmail));

        map.get("park").stream()
            .forEach(System.out::println);

        //조회수의 평균
        System.out.println();
        System.out.println(">>조회수의 평균");
        double avg = blogs.stream()
            .mapToInt(BlogResponseDTO::getViewCnt)
            .average()
            .orElse(0);
        System.out.println(avg);

        //조회수의 평균
        System.out.println();
        System.out.println(">>>debug >>> 이메일 추출하는데 중복되는 데이터는 한번만 출력");
        blogs.stream()
            .map(BlogResponseDTO::getEmail)
            .distinct()
            .forEach(System.out::println);
        
        System.out.println();
        System.out.println(">>>sort, reversed");
        blogs.stream()
            .sorted(Comparator.comparing(BlogResponseDTO::getViewCnt).reversed())
            .forEach(System.out::println);
        
        System.out.println();
        System.out.println(">>>anymatch(존재여부 판단), allMatch(검증)");
        System.out.println(">>>블로그 작성자(lim)의 존재를 판단");
        boolean isEmail = blogs.stream()
            .anyMatch(blog->blog.getEmail().equals("lim"));
        System.out.println(isEmail);

        boolean isExists = blogs.stream()
            .allMatch(blog->blog.getViewCnt()>=20);
        System.out.println(isExists);

        boolean isUnder = blogs.stream()
            .noneMatch(blog->blog.getViewCnt()<10);
        System.out.println(isUnder);

        /*
        Optional
        -메서드 호출시 반환값을 확신할 수 없을때 사용하는 문법 
        -null,method() : null 값 처리를 놓쳐서 발생하는 예외를 피하고자 할 떄
        -메서드의 반환타입으로만 사용(전역변수, 매개변수)
        -사용의도에 맞게 사용해야 함(null 할당 x)
        */

        System.out.println();
        System.out.println(">>>>optional");
        Optional<String> op = Optional.of("lgcns");
        // if(op.isPresent()){
        //     System.out.println(op.get());
        // }
        // op.ifPresent(value -> System.out.println(value));

        op.ifPresentOrElse(
            value -> System.out.println(value), 
            () -> System.out.println("값이 없다"));

        op = Optional.empty();
        String err = op.orElseThrow(()-> new RuntimeException("값이 없다"));
        System.out.println(err);
        
    }
}