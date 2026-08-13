package features.oop.sub;

import features.oop.sup.PersonDTO;

public class ManagerDTO extends PersonDTO{

    private String dept;

    public ManagerDTO(){

    }


    public ManagerDTO(String name, int age, String address, String dept) {
        super(name, age, address);
        this.dept = dept;
    }


    public String getDept() {
        return dept;
    }


    public void setDept(String dept) {
        this.dept = dept;
    }
    
    @Override
    public String PerosnInfo(){
        return super.PerosnInfo()+", dept="+dept;
    }
}
