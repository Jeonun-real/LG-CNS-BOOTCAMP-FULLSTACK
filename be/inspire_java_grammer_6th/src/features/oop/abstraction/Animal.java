package features.oop.abstraction;

public abstract class Animal {

    private String name;

    public Animal(){

    }

    public void eating(String food){
        System.out.println(food+"를 먹고 살아갑니다");
    }

    // public abstract void fly();

    // public abstract void takeOff();
    
    // public abstract void landing();

}