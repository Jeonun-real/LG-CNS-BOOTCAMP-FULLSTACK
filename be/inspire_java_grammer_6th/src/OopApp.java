import features.oop.service.OopService;
import features.oop.sub.ManagerDTO;
import features.oop.sub.StudentDTO;
import features.oop.sub.TeacherDTO;
import features.oop.sup.PersonDTO;
import features.oop.util.Flag;

public class OopApp {
    public static void main(String[] args) {
        
        // StudentDTO stu = new StudentDTO();
        // stu.setSsn("2026");
        // stu.setName(null);
        // stu.setAge(0);
        // stu.setAddress(null);

        StudentDTO stu = new StudentDTO("이름",20,"서울","2026");
        System.out.println(stu.getName());
        System.out.println(stu.getAge());
        System.out.println(stu.getAddress());
        System.out.println(stu.getSsn());

        System.out.println();
        System.out.println("teacherDTN");

        TeacherDTO tea = new TeacherDTO("이름",40, "서울","수학");
        System.out.println(tea.getName());
        System.out.println(tea.getAge());
        System.out.println(tea.getAddress());
        System.out.println(tea.getSubject());
        
        System.out.println();
        System.out.println("변수타입의 다형성");
        PersonDTO manager = new ManagerDTO("이름",20, "서울","교육사무국");

        // manager.get 접근 방법
        //casting이 참조타입에 적용될수 있음(다만, 상속관계를 전제로)
        System.out.println(((ManagerDTO)manager).getDept());

        System.out.println();
        System.out.println("변수타입의 다형성: 배열");

        PersonDTO [] ary = new PersonDTO[3];
        ary[0] = new StudentDTO("서정운",20,"서울", "2026");
        ary[1] = new ManagerDTO("이세연",20,"서울","교육팀");
        ary[2] = new TeacherDTO("이하연",20,"서울","react");

        PersonDTO per01 = ary[0] ;
        System.out.println(per01.getName());
        System.out.println(per01.getAddress());
        System.out.println(per01.getAge());
        System.out.println(((StudentDTO)per01).getSsn());

        System.out.println();
        // for(int i =0;i<ary.length;i++){
        //     PersonDTO per = ary[i];
        //     if(per instanceof TeacherDTO){
        //         System.out.println(((TeacherDTO)per).getSubject());
        //     }
        //     if(per instanceof ManagerDTO){
        //         System.out.println(((ManagerDTO)per).getDept());

        //     }     
        //     if(per instanceof StudentDTO ){
        //         System.out.println(((StudentDTO)per).getSsn());

        //     }            
        // }
        System.out.println("메서드의 다향성 ");
        for(int i =0;i<ary.length;i++){
            PersonDTO per = ary[i];
            System.out.println(per.PerosnInfo());
            

        }

        System.out.println();
        System.out.println();
        System.out.println("매개변수의 다양성");
        System.out.println();

        OopService service = new OopService();
        // service.setAry(stu);
        // service.setAry(tea);
        // service.setAry(manager);        

        service.makePerson(Flag.STUDENT, "서", 27, "SEOUL", "2026");
        service.makePerson(Flag.TEACHER, "정", 26, "SEOUL", "JAVA");
        service.makePerson(Flag.MANAGER, "정", 25, "SEOUL", "교육팀");

        PersonDTO[] result = service.getAry();
        for(PersonDTO person : result){
            if(person==null){
                break;
            }
            System.out.println(person.PerosnInfo());
        }

        System.out.println();
        System.err.println(">>>>>findperson");
        PersonDTO find = service.findPerson("서");
        if(find != null){
            System.out.println(find.PerosnInfo());
        }else{
            System.out.println(">>>>not found");
        }



    }
}

