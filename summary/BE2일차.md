# BE 2일차 — Java 핵심 문법

has a 방식
class <- class
        instance(var, method) 

App 는 start, end point를 가짐

[{}, {}, {}] array => collection
json의 객체 하나가 DTO
사용자로 부터 보내는 요청정보 request
값을 담아서 사용자로부터 보내는 정보 response


source action으로 constructore로 바로 생성자 생성가능


public BlogResponseDTO register(BlogRequestDTO request)
반환값은 주소값을 가지는 객체도 가능
매개변수도 객체형태로 받기 가능

사용자가 요청정보를 props로 묶어서 보내면 

instance -> method 에서 instance 생성 -> instance가 반환

하나의 클래스에 같은 타입을 같은 methon 호출 가능?
가능 -> 메서드 오버로딩 
단 매개변수의 타입과 개수가 달라야됨