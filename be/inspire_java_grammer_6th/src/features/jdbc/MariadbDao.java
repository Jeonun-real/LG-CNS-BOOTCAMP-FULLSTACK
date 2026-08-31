package features.jdbc;

import java.sql.Connection; //interface connnection 이 있는 추상클래스
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.DESKeySpec;

import features.jdbc.domain.dto.DepartmentRequestDTO;
import features.jdbc.domain.dto.DepartmentResponseDTO;

public class MariadbDao {
    /*
    package : java.sql.*
    jdbc설치
     - 드라이버관리 및 연결 작업  - driver loading
     - 커넥션 작업 - connection
     - sql 문장 수행을 위한 작업 - statement(query)
     - 실행 작업 - execute
     - 결과를 핸들링하는 작업 -resultset
     - 연결 종료 = close
    */

    // 이건 private 한거니까 .env로 꺼내야 된다(보안)
    // spring boot에는 설정에서 쓰임
    // 수정될수 없도록 상수로 선언
    private static final String DRIVER = "org.mariadb.jdbc.Driver";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:mariadb://localhost:3306/lgcns"; 



    public MariadbDao(){
        try{
            Class.forName(DRIVER); //xx.jar 파일 연결 -> driver loading
            System.out.println("driver loading ok");
        }catch(Exception e){

        }finally{
            try{

            }catch(Exception e){
                
            }
        }
    }

    public List<DepartmentResponseDTO> departments() {
        Connection          conn = null;
        PreparedStatement   pstmt = null;
        ResultSet           rset = null; // 가상의 테이블
        String sql = """
                        select * 
                        from department
                    """;
        List<DepartmentResponseDTO> list = new ArrayList<>();



        try{
            conn = DriverManager.getConnection(URL,USER,PASSWORD); //user, password, url => 주로 static으로 선언된 상수값
            System.out.println("conn ok" + conn);

            pstmt = conn.prepareStatement(sql);
            rset = pstmt.executeQuery(); //tuple를 읽는 느낌이라 커서를 record가 없을때까지 한칸씩 이동해야 됨
            while(rset.next()){
                list.add(DepartmentResponseDTO.builder()
                    .dept_id(rset.getString(1))
                    .dept_name(rset.getString(2))
                    .loc_id(rset.getString(3))
                    .build());
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(conn != null)
                    {conn.close();}
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        return list;
        
    }

    public int update(DepartmentRequestDTO request) {
        Connection          conn  = null ; 
        PreparedStatement   pstmt = null ; 
        String  sql = """
                update  department
                set     dept_name = ?
                where   dept_id   = ?
                """;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD); 
            System.out.println("debug >>>> conn ok!! "+conn); 

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, request.getDept_name());
            pstmt.setString(2, request.getDept_id()) ;
            return pstmt.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if( conn != null) { conn.close(); } 
            } catch(Exception e) {
                e.printStackTrace(); 
            }
        }
        return 0 ;
    }

}
