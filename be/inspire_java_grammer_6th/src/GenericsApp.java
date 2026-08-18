import java.util.ArrayList;
import java.util.List;

import features.generics.ResponseTemplate;
import features.oop.sub.ManagerDTO;
import features.oop.sub.StudentDTO;
import features.oop.sub.TeacherDTO;
import features.oop.sup.PersonDTO;

public class GenericsApp {
    public static void main(String[] args) {
        System.out.println();
        System.out.println("GenericsApp");

        // generic의 wildcard : extends vs super
        // 메서드의 타입선언 및 리턴타입을 지정할때 사용되는 문법
        // extends : 읽기 전용
        // suepr : 쓰기전용

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
        

        //입력성공: 201, created, xxxDTO
        // ResponseTemplate<PersonDTO> response = 
        //     new ResponseTemplate<PersonDTO>(201,"created",student);   

        //목록조회: 200, ok, list
        ResponseTemplate<List<PersonDTO>> response = 
            new ResponseTemplate<List<PersonDTO>>(200,"ok",personList);   

        List<PersonDTO> lst = response.getData();
        for(int i=0; i <lst.size();i++){
            PersonDTO person = lst.get(i);
            System.out.println(person.PerosnInfo());

        }



    }
}
