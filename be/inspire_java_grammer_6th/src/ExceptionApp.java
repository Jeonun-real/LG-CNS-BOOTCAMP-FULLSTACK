import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import features.exception.ExceptionDemo;
import features.exception.util.InspireException;

public class ExceptionApp {

    public static void main(String[] args) {
        // System.out.println("debug>>>>main start");

        // String [] strAry = {"jslim","inspire","lgcns"};
        // try{
        //     for(int i=0;i<=strAry.length;i++)
        //         System.out.println(strAry[i]); 
        // }catch (Exception e){ 
        //     System.out.println("debug>>>catch 예외시에만 수행");
        //     e.printStackTrace();
        // }finally{
        //     System.out.println("debug>>>예외발생여부와 상관없이 수행");
        // }
        
        // System.out.println("debug >> compile exception");
        
        // // input stream : 데이터가 입력되는 통로
        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // String line = null;
        // try {
        //     System.out.print("메세지를 입력하세요: ");
        //     line = br.readLine();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        
        // System.out.println(line);

        System.out.println();
        ExceptionDemo demo = new ExceptionDemo();
        try{
            demo.first(-1);
        }catch(InspireException e){
            e.printStackTrace();
        }

        System.out.println("debug >> main end");
        
    }
}