package features.oop.sup;

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
public class PersonDTO {
    private String name;
    private int age;
    private String address;

    // public PersonDTO(){

    // }

    public PersonDTO(String name, int age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    // public String getName() {
    //     return name;
    // }

    // public void setName(String name) {
    //     this.name = name;
    // }

    // public int getAge() {
    //     return age;
    // }

    // public void setAge(int age) {
    //     this.age = age;
    // }

    // public String getAddress() {
    //     return address;
    // }

    // public void setAddress(String address) {
    //     this.address = address;
    // }

    public String PerosnInfo(){
        return "name = "+name + ", age = "+age+ ", address ="+address ;
    }


    

}
