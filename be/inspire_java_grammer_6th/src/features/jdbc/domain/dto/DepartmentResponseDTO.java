package features.jdbc.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString

// 테이블과 연결되는 객체(웬만하면 db랑 같은 이름으로 명세)
public class DepartmentResponseDTO { 
    private String dept_id, dept_name, loc_id;

}
