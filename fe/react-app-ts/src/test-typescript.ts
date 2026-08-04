let userName: string = "JohnDoe";
let age: number = 30;
let isMarriage: boolean = false;

console.log('Type of username:', typeof userName);
console.log('Type of age:', typeof age);
console.log('Type of isMarriage:', typeof isMarriage);  

//array
let ary: string[] = ["apple", "banana", "cherry"];


//객체타입을 선언하고 변수의 타입으로 사용해야 함(interface)
interface User {
    email: string;
    password: string;
    address?: string; // optional property (address 가 없어도 오류 x)
}

let user: User = {
    email: "john.doe@example.com",
    password: "securePassword123"
};
console.log('User:', user);

let userAry: User[] = [
    {
        email: "john.doe@example.com",
        password: "securePassword123"   
    },
    {
        email: "jane.smith@example.com",
        password: "anotherSecurePassword456",
        address: "seoul"
    }
];
console.log('User Array:', userAry);

//함수(매개변수 타입, 리턴타입 지정)
function showMessage(name: string): string {
    return `Message: ${name}`;
}
showMessage("Hello, TypeScript!");

//union type 타입 2개 가능
let userStatus: string | number;
userStatus = "active"; // valid
userStatus = 1; // valid
console.log('union type:', userStatus);

