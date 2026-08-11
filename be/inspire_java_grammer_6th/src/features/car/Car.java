package features.car;

public class Car {
    
    private String brand;
    private String model;

    public Car(){

    }
    
    /*
    constructor overloading
    - 매개변수 타입과 갯구를 달리해서 선언된 것
    - 매개변수로 전달된 값을 멤버변수에 초기화
    - this는 인스턴스 생성 전 인스턴스를 지칭하는 키워드
    */
    public Car(String brand, String model){
        this.brand = brand;
        this.model = model;

    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    //반환값o 매개변수x
    public String carInfo(){
        return brand+"\t"+model;
    }

}
