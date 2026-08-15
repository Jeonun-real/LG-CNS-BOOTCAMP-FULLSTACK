package features.oop.service;

import features.oop.sub.ManagerDTO;
import features.oop.sub.StudentDTO;
import features.oop.sub.TeacherDTO;
import features.oop.sup.PersonDTO;
import features.oop.util.Flag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/*
- service는 비지니스 로직 처리
- 배열을 통한 xxxxDTO를 관리
- seach, delete, insert, update 요청 처리 
- 


*/
@SuperBuilder
@Setter
@Getter
@ToString
public class OopService {
    

    private PersonDTO [] ary ;
    private int idx;

    public OopService(){
        ary = new PersonDTO[10];
        idx = 0;
    }
    
    public void setAry(PersonDTO per){
        ary[idx++]=per;
    }

    //copy (shallow copy, deep copy)
    public PersonDTO [] getAry(){
        return ary;
    }
    
    /*
    CRUD
    Params: flag, name, age, address, comm(ssn, subject, dept)
    flag : 1 > student, 2 > techer, 3 > Manager
    makePerson() 매개변수로 전달된 값을 가지고 타입에 맞는 객체를 생성하고 배열 담는 역할
    */
    public void makePerson(Flag flag, String name, int age, String address, String comm){
        System.out.println(">>>emun flag:" + flag );
        System.out.println("flag getxxxx:"+flag.getFlag());
        // switch (flag.getFlag()) {
        //     case 1 -> System.out.println("학생");
        //     case 2 -> System.out.println("강사");
        //     case 3 -> System.out.println("매니저");
        // }
        PersonDTO per = 
            (flag.getFlag() ==1 ) 
            ?
                StudentDTO.builder()
                    .name(name).age(age).address(address)
                    .ssn(comm).build()
            : (flag.getFlag()==2) ?
                TeacherDTO.builder()
                    .name(name).age(age).address(address)
                    .subject(comm).build()
            :   ManagerDTO.builder()
                .name(name).age(age).address(address)
                .dept(comm).build();               
        
        setAry(per);



    }


    /*
    params : name
    method : findperoson
    return : personDTO
    Q) 이름을 전달받아서 일치하는 이름을 가진 PerosnDTO 를 리턴
    */
    public PersonDTO findPerson(String name){
        PersonDTO result = null;

        for(PersonDTO person:ary){
            if(person==null){
                break;
            } else{
                if(person.getName().equals(name)){
                    return person;
                }
            }
        }

        return result;
    }

}
