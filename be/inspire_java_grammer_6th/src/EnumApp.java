import features.oop.util.Flag;

public class EnumApp {
    public static void main(String[] args) {
        Flag flag = Flag.STUDENT;
        System.out.println(flag);
        System.out.println(flag.getFlag());

        System.out.println(">>>>swich 활용");
        switch (flag) {
            case STUDENT -> System.out.println("학생");
            case TEACHER -> System.out.println("강사");
            case MANAGER -> System.out.println("매니저");
        }

        System.out.println(">>>>==비교");
        if(flag==Flag.STUDENT){
            System.out.println("학생");
        }
    }
}
