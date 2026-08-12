public class StringApp {
    public static void main(String[] args) {
        // String str01 ="lgcns";
        // String str02 = "lgcns";

        String str01 = new String("lgcns");
        String str02 = new String("lgcns");

        if(str01 == str02){
            System.out.println("str01==str02");
        }else{
            System.out.println("str01!=str02");
        }
    }
}
