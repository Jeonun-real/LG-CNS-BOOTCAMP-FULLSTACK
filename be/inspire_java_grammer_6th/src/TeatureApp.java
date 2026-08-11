import features.var.Teacher;

public class TeatureApp {
    public static void main(String[] args) {
        Teacher teacher = new Teacher();
        System.out.println(teacher);
        
        // 인스턴스 소유 메서드 접근
        teacher.setName("서정운");
        String name = teacher.getName();
        System.out.println(name);


        // 인스턴스 소유 변수 접근
        teacher.name = "inspuire";
        System.out.println(name);

        
    }
    
}
