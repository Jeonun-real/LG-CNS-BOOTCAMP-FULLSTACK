import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import features.blogs.domain.dto.BlogResponseDTO;

public class IOStreamApp {
    public static void main(String[] args) {
        // System.out.println(">>>> io stream을 이용한 데이터 입출력");
        // try {
        //     int input = System.in.read();
        //     System.out.println(input);
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        // System.out.println( "io stream을 이용한 데이터 입출력 : char");

        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // try{
        //     String input = br.readLine();
        //     System.out.println(input);
        // }catch(IOException e){
        //     e.printStackTrace();
        // }finally{
        //     try{
        //         if(br!=null){

        //             br.close();
        //         }
        //     }catch(Exception e){
        //         e.printStackTrace();
        //     }
        // }

        // System.out.println(">>>>autocloseable");
        // try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
        //     System.out.println(br.readLine());
        // }catch(Exception e){
        //     e.printStackTrace();
        // }

        //Inputstream -> inputstreamReader, BufferedReader(fileReader(file()))


        // System.out.println("file input/output :");
        // String path = "./be/inspire_java_grammer_6th/test.txt";
        // try(BufferedReader br = new BufferedReader(new FileReader(new File(path),StandardCharsets.UTF_8))){
        //     String input = null;
        //     while((input=br.readLine())!=null){
        //         System.out.println(input);
        //     }
        // }catch(Exception e){
        //     e.printStackTrace();
        // }

        // System.out.println("file input/output :");
        // String path = "./be/inspire_java_grammer_6th/test.txt";
        // try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path),StandardCharsets.UTF_8))){
        //     bw.write("덮어쓴다");
        // }catch(Exception e){
        //     e.printStackTrace();
        // }

        



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

        // System.out.println(">>>>ArrayList<BlogResponseDTO> Serializable");
        // String path = "./be/inspire_java_grammer_6th/object.txt";
        // try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)))){
        //     oos.writeObject(blogs);
        // }catch(Exception e){
        //     e.printStackTrace();
        // }
        String path = "./be/inspire_java_grammer_6th/object.txt";
        try(ObjectInputStream ojs = new ObjectInputStream(new FileInputStream(new File(path)))){
            List<BlogResponseDTO> list = (List<BlogResponseDTO>)ojs.readObject();
            list.forEach(System.out::println);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
