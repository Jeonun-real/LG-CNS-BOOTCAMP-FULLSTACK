package features.oop.sub;

import features.oop.sup.PersonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString

public class StudentDTO extends PersonDTO{

    private String ssn;

    // public StudentDTO(){
    // }



    // public String getSsn(){
    //     return ssn;
    // }


    // public void setSsn(String ssn){
    //     this.ssn=ssn;
    // }

    public StudentDTO(String name, int age, String address, String ssn) {
        super(name, age, address);
        this.ssn = ssn;
    }

    @Override
    public String PerosnInfo(){
        return super.PerosnInfo()+", ssn="+ssn;
    }






}
