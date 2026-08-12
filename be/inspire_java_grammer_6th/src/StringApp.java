public class StringApp {
    public static void main(String[] args) {
        // String str01 ="lgcns";
        // String str02 = "lgcns";

        String str01 = new String("lgcns");
        String str02 = new String("lgcns");

        System.out.println(str01);
        System.out.println(str02);



        if(str01==str02){
            System.out.println("str01==str02");
        }else{
            System.out.println("str01!=str02");
        }

        if(str01.equals(str02)){
            System.out.println("str01==str02");
        }else{
            System.out.println("str01!=str02");
        }

        StringBuffer sb = new StringBuffer(str01);
        sb=sb.append("!!");
        sb=sb.append("!!");
        sb=sb.append("!!");
        sb=sb.append("!!");
        String s = sb.toString();

        System.out.println(s);

    }
}
