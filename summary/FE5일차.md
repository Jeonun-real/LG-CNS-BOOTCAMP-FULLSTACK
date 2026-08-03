# FE 5일차 핵심 개념 정리

script가 script를 import 가능 <-모듈이해

script는 head 보다 ui렌더링이 끝난후 진행하기 위해 body에 선언


실습 sceleton.html, css. js 

container.innerHTML = items.map().join(""); 이거랑
items.forEach((obj,idx)=>{}); 이거랑 같음

container.innerHTML = items.map().join("");
items.map() => 배열ㄹ로 데이터를 꺼내서 join을 통해 조작

const rederCard = (items)=>{

}

const rederCards = (items)=>{

    container.innerHTML = items.map(rederCard).join("");

}
함수가 함수를 호춝가능
items.map의 요소를 함수로 전달 가능
함수()로 안쓰는 이유는 함수(매개변수)느낌이기 떄문에
rederCard()가 아니라 rederCard임




[{…}, {…}, {…}, {…}, {…}, {…}]
[{}]을 items로 받아오고 map을 통해 item을 꺼낸다.


const rederCard = (item)=>{
    return `
        <div class='card'>
            <img class = 'card-img>
            <div class = 'card-body'>
            </div>
        </div>
    `

}

promise.all 여러개의 axios 통신 가능
    const [response] = await Promise.all([        
        axios.get('../server/data.json').then().catch(),
        axios.get('../server/data.json').then().catch(),
        axios.get('../server/data.json').then().catch(),
        axios.get('../server/data.json').then().catch(),
        axios.get('../server/data.json').then().catch()

    ]);


애니메이션 설정
.card {
    animation: fadeIn 0.4s ease;
}
@keyframes fadeIn{
    from {opacity: 0; transform: translate(6px);}
    to {opacity: 1; transform: translate(0px);}
}

로딩시 애니메이션 구축
.skeleton-img, .skeleton-line {
    background: linear-gradient(
        90deg,
        #e8e8e8 25%,
        #f2f2f2 37%,
        #e8e8e8 63%
    );
    background-size: 400% 100%;
    animation: shimmer 1.4s ease infinite;
}   
    로딩시 반짝거리면 수평선으로 이동하는 느낌

        grid-template-columns: repeat();
반응형 아닌데 반응형향 첨가

    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
1100넘으면 반응x


#####################################
html안에 component 구성

component는 .js로 구성
js확정버전인 jsx(js extensiotn)를 사용하면
script 범위내에서 태그로 html css 호출가능
javascript + html/xml



다른 파일에 있는 스크립트 기능을 특정 파일에서 사용할수 있는것 :module
관계는 import 와 export로 구성

a.js b.js가 있고

a.js의 함수 및 변수를 b.js에서 사용하고 하고자 하는데
a.js가 export 되야 가능 b.js에서 import 되야 가능
그래서 script를 모듈이라 하는거

const getTotal = () =>{
    return total;
}

export default getTotal;
함수 하나를 만들고 함수 하나를 export

const a = () =>{
    return a;
}

const b = () =>{
    return b;
}

export default a, b;

여러개 한번에 보내는건 불가능 그래서
export const a = () =>{
    return a;
}

export const b = () =>{
    return b;
}
이렇게 개별 export 필요

export는 import로 이렇게 받을수 있음
import a from './a.js' = export default 로 1개만 보냈을때, 변수 이름 변경 가능

import {a,b} from './a.js' = export = 일반 export 여러개 보냈을때, 중괄호, 이름변경 불가(as 쓰면 가능)

import와 export를 쓰려면 반드시 script의 type을 모듈로 명시 

그래서 <script type='module' scr="js링크"></script>로 선언한뒤
import {a,b} from './a.js' 로 import

es6.hrml 문법!!!!!!!!!
            //spread opeator
            //객체 또는 배열값을 다른 객체, 배열로 복제하거나 옮길때 사용
            //단순 = 시 주소값을 할당받아 같은 객체를 참조하게 됨
            //그래서 하나바꾸면 바꾸지 않은 오리지널도 변경됨

            let objOriginal ={
                a :10,
                b:20
            };

            let objNew = objOriginal;
            objNew.a =20;

            => 이러면 주소 복사로 original과 복사본 둘다 변경

            objNew = {...objOriginal};
            objNew.a=20;

            => 이러면 spread opeator 값만 복사

            let objOriginal ={
                a :10,
                b:20
            };

            let ary = [1,2,3];
            let newAry = {...ary};
            이렇게 배열도 가능






            //구조분해
            //변수의 매개변수에 {}(중괄호)있으면 구조분해 문법
            //원래는 props.a, props.b인데 구조분해로 간략화됨
            let{a,b} = objOriginal

            //oprtional chaining
            const user ={
                name:{
                    first :'lim',
                    last : 'js'
                },
                address : {
                    city: 'seoul',
                    postcode: 'asdfasfasf',
                }
            }
            이 객체에서
            const postcode = user.address && user.address.postcode;
            일반적인 유효성 체크 문법
            const postcode = user.address?.postcode;
            oprtional chaining 적용
            ?.앞에 속성을 확인해서 없으면 undifined 반영 -> 다음스텝 진행
            user.address가 있으면 postcode를 가져온다.
            3항 연산자 느낌스

            //널 병합 연산자(??)
            //디폴트 값(초기화값) 설정
            const city = user.address?.city?? 'busan';

            optional chaining과 널병합연산자는 중간에 err로 멈추지 않게 하기 위해 필요

            //enhanced object literal : 
            //축약문법
            let lang = 'lang'
            let subject = {
                lang :lang
            }

            키랑 벨류가 동일하므로
            하나로 축약가능
            let lang = 'lang'
            let subject = {
                lang
            }

            함수도 축약해버림
            let lang = 'lang'
            let subject = {
                lang,
                coding(){
                    console.log('coding');
                }
            }





######################################
react = script 라이브러리

script라이브러리를 활용해 재사용성이 높은 UI 컴포넌트 생성

빠른업데이트와 렌더링 속도

핵심개념 virtual dom
기존: dom를 script로 건듬
react: dom위에 vitual dom이 씌워져 있음
virtual dom만 검드림
dom이 계층구조로 이루어져서 state 변경 발생시 기존에 node walking을 통해 들어가서 변경해야 한다면
virtual dom은 변경 발생시 실시간 변경부분 감지 후 빠르게 해당 부분만 direct access로 찾아서 변경

핵심개념2:component-based
***
react에서는 function을 component로 표현

function의 return 값을 element

fuction의 매개변수는 props
***
fuction 하나가 component고 
props 전달받아
브라우저의 렌더링 요소인 element 반환

그래서 component-base는 가장 작은 블록 단위가 되어야 하며
이를 page component가 composition(통합)하여 browser의 페이지 구축

props를 통해서 데이터를 꺼내야된다. -> 구조분해를 통해 작업 가능

핵심개념3: 재사용성
재사용성이 높은 component를 만들어 내야 하는데 
이를 통해 UI를 생성

프로그램 재사용성: 독립적으로 사용가능
module을 그냥 쓰고 싶으면 그것만 쓰고 싶으면 가져다가 사용가능해야됨
다른 module과 의존성(dependency)이 있다면 재사용성 낮음
의존성이 있다면 버전 문제 등 문제 발생

nopde = js를 브라우저 밖에서도 실행하게 해주는것
node의 package manager = npm

git - local command
github - remote repository

react project 만들기
CRA(create react app) = react project를 만드는 도구
vite 기반은 type script
=> npx create-react-app <project name>
npx                create-react-app       my-app
실행 도구           실행할 패키지           만들 폴더 이름

npm install -g xxxx:version 필요한 버전 설치
=> python의 pip install이랑 같은거
npm uninstall -g xxxx:latest

react는 spa
html 공간 1개

react - component - element -> html 렌더링
page 리로딩 없이 component 변경만으로도 chage
src > index.js
=>const root = ReactDOM.createRoot(document.getElementById('root'));
버츄얼 돔 생성

import App from './App';
<App /> = 컴포넌트, 모듈 임포트

app.js > export
index.js < import

component를 index.js에 적고 index.html에 렌더링

스크립트 템플릿 문법
{}로 정의하고 app으로 

프론트도 하나의 서버
서버기동
프론트는 3000번
백은 8080번

백에 프론트의 코드인 static(html, css, js)을 moving 시키면
백만 베포하면 되긴함

근데 보통 프론트, 백 각각 베포해서 cors 문제가 발생
cors 문제: 브라우저가 “다른 출처에서 온 요청인데, 서버가 허용한다고 말하지 않았다”며 응답 사용을 차단하는 오류

port = 포트번호 npm start
포트번호바꿔서 구동하기

js파일은 단어의 첫글자는 대문자로 이어지게
SingIn

return을 할때는 최소 하나의 태그 필요


Invalid DOM property `class`. Did you mean `className`?
class를 적용할때 충돌나서 문제생김

