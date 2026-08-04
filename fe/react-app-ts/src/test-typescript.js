"use strict";
const userName = "JohnDoe";
const age = 30;
const isMarriage = false;
console.log('Type of username:', typeof userName);
console.log('Type of age:', typeof age);
console.log('Type of isMarriage:', typeof isMarriage);
//array
const ary = ["apple", "banana", "cherry"];
const user = {
    email: "john.doe@example.com",
    password: "securePassword123"
};
console.log('User:', user);
let userAry = [
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
function showMessage(name) {
    return `Message: ${name}`;
}
showMessage("Hello, TypeScript!");
//union type 타입 2개 가능
let userStatus;
userStatus = "active"; // valid
userStatus = 1; // valid
console.log('union type:', userStatus);
