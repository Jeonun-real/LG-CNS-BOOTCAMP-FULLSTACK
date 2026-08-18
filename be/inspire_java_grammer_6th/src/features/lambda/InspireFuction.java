package features.lambda;

/*
함수형 인터페이스
- 인터페이스가 가질수 있는 메서드가 딱 하나인걸 의미 
- 람다식을 활용하기 위해서

Supplier: 매개변수x 반환타입 0
Cosumer : 매개변수o,반환타입 x
function: 매개변수o, 반환타입 o
perdicate: 매개변수o, 반환타입 boolean
*/

@FunctionalInterface
public interface InspireFuction {
    public int max(int x, int y);
    

    
    
} 
