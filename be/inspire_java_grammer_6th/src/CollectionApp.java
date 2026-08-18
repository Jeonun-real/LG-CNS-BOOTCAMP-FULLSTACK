import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import features.oop.sub.ManagerDTO;
import features.oop.sub.StudentDTO;
import features.oop.sub.TeacherDTO;
import features.oop.sup.PersonDTO;
import features.var.Teacher;

public class CollectionApp {
    
    /*
    collection api(java.util.*)
    intetface -> 객체생성 불가
    - list(중복허용, 순서존재, 요소 타입으로 객체만, 가변길이)
    - set(중복허용x, 순서 존재x, 요소의 타입으로 객체만, 가변길이)
    - map({key:value},{key:value},....)

    Boxing과 UnBoxing
    Wrapper class(primitive type->reference type, reference type <- primitive type)
    - int - Integer
    - chat - Character

    Generics (파라미터 타입)
    T type
    E element(collection 컴파일 시점의 타입의 안정성, 불필요한 캐스팅을 지양)
    K key
    V value
    N number
    
    */
    public static void main(String[] args) {
        
        System.out.println("debug array");

        int [] intAry={1,2,3,4,5};

        System.out.println(intAry.length);
        System.out.println(Arrays.toString(intAry));

        List<String> list = new ArrayList<String>();
        list.add("String");
        
        //generic 문법에 의한 error
        // list.add(10);
        // list.add(true);
        System.out.println("list>>>"+list.size());
        System.out.println("list>>>"+list);
        for(int idx = 0 ; idx<list.size() ; idx++){
            String data = list.get(idx);
            System.out.println(data);
        }

        System.out.println();
        System.out.println("xxxxDTO");
        List<PersonDTO> personList = new ArrayList<PersonDTO>();
        StudentDTO student = StudentDTO.builder()
                                    .name("inspire").build();
        TeacherDTO teacher = TeacherDTO.builder()
                                    .name("jslim").build();
        ManagerDTO manager = ManagerDTO.builder()
                                    .name("lgcns").build();

        personList.add(student);
        personList.add(teacher);
        personList.add(manager);

        //명령형 처리
        // List<String> filteringList = new ArrayList<String>();
        // for(int idx = 0 ; idx<personList.size() ; idx++){
        //     PersonDTO person = personList.get(idx);
        //     if(person.getName().startsWith("j")){
        //         filteringList.add(person.getName().toUpperCase());
        //     }
        // }

        //Stream API (선언적 처리)
        //컬렌션을 STREAM 객체로 바인딩하고 - 중간연산(0~N) - 연산결과를 최종연산(1)
        // List<String> filteringList = personList.stream()
        //                                 .filter(s->s.getName().startsWith("j"))
        //                                 .map(s->s.getName().toUpperCase())
        //                                 .collect(Collectors.toList());
        // System.out.println(filteringList);

        System.out.println(">>>>stream");
        personList.stream()
            .filter(person->person.getName().length()>5)
            // .forEach(System.out::println);
            .forEach(person-> System.out.println(person.PerosnInfo()));


        System.out.println();
        System.out.println(">>>>>set");
        Set<String> set = new HashSet<>();
        set.add("jslim");
        set.add("inspire");
        set.add("lgcns");
        set.add("jslim");
        System.out.println(set);
        Object [] setAry = set.toArray();
        for(Object data : setAry){
            System.out.println(data);
        }

        System.out.println();
        System.out.println("Map(key:value) === JSON");

        List <StudentDTO> studentList = new ArrayList<StudentDTO>();
        List <TeacherDTO> teacherList = new ArrayList<TeacherDTO>();
        List <ManagerDTO> ManagerList = new ArrayList<ManagerDTO>();

        Map<String, List<? extends PersonDTO>> map = new HashMap<>();
        map.put("student",studentList);
        map.put("teacher",teacherList);
        map.put("manager",ManagerList);

        // List<? extends PersonDTO> mapList = map.get("teacher");
        // mapList.forEach(person->System.out.println(person.PerosnInfo()));

        // map.get("student")
        // .stream()
        // .filter(null)
        // .map(null)
        // .forEach(person->System.out.println(person.PerosnInfo()));
    }
}
