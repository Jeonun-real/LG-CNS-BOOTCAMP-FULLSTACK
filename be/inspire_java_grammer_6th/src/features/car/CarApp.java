package features.car;

public class CarApp {
    public static void main(String[] args) {
        Car car = new Car();
                               
        car.setBrand("benz"); 
        System.out.println("brand" + car.getBrand());


        ////////
        Car benz = new Car("Benz", "c200");
        System.out.println(benz.getBrand());
        System.out.println(benz.getModel());

        String carInfo = benz.carInfo();
        System.out.println("carinfo"+carInfo);

          
    }
    
}
