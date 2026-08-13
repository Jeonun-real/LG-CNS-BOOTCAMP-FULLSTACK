package features.oop.sub;

import features.oop.sup.PersonDTO;

public class StudentDTO extends PersonDTO{

    private String ssn;

    public StudentDTO(){
    }



    public String getSsn(){
        return ssn;
    }


    public void setSsn(String ssn){
        this.ssn=ssn;
    }

    @Override
    public String PerosnInfo(){
        return super.PerosnInfo()+", ssn="+ssn;
    }



    public StudentDTO(String name, int age, String address, String ssn) {
        super(name, age, address);
        this.ssn = ssn;
    }


}
