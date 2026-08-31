import java.util.List;

import features.jdbc.MariadbDao;
import features.jdbc.domain.dto.DepartmentRequestDTO;
import features.jdbc.domain.dto.DepartmentResponseDTO;

public class jdbcApp {
    public static void main(String[] args) {
        MariadbDao dao = new MariadbDao(); 
        System.out.println("select");
        List<DepartmentResponseDTO> list = dao.departments();
        list.stream()
            .forEach(System.out::println);

        System.out.println();
        System.out.println("dml");

        DepartmentRequestDTO request =
            DepartmentRequestDTO.builder()
            .dept_id("90")
            .dept_name("해외영업3팀")
            .build();
        int flag = dao.update(request);
        System.out.println("reult flag:"+flag);
    }
}