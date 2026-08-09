import React, { useState }  from 'react';
import styled               from 'styled-components';
import { Link, useNavigate } from "react-router-dom";
import api from '../../../api/axios';

const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f2f2f2;
`;

const FormWrapper = styled.div`
  background-color: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0px 8px 16px rgba(0,0,0,0.1);
  width: 400px;
`;

const Title = styled.h2`
  text-align: center;
  margin-bottom: 20px;
  color: #333;
`;

const Input = styled.input`
  width: 100%;
  padding: 12px;
  margin-bottom: 15px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 16px;

  &:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 5px rgba(0,123,255,0.3);
  }
`;

const Button = styled.button`
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  font-size: 16px;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 10px;

  &:hover {
    background-color: #0056b3;
  }

  &:disabled {
    background-color: #aaa;
    cursor: not-allowed;
  }
`;

const TextLink = styled(Link)`
  display: block;
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
  color: #007bff;
  text-decoration: none;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
`;

// npm install styled-components @mui/material @mui/icons-material @emotion/react @emotion/styled

const SignUpPage = () => {

    const [form, setForm] = useState({
        name : '',
        email : '',
        password : ''
    }) ;

    // 기존값을 유지하면서 현재 입력된 필드에 대한 상태변화(업데이트)를 처리 
    const keyHandler = (e) => {
        const {name, value} = e.target ;
        setForm({...form, [name]:value});
    };

    const moveUrl = useNavigate();

    const signUpHandler = async (e) => {
        e.preventDefault();
        console.log(`debug >>>> SignUpPage signUpHandler event`); 
        const data = {...form} ; 
        /*
        Q)
        - npx json-server --watch db.json --port 5000 
        - axios(/users) 통신을 통해서 json-server 데이터를 전달하고 저장
        - post, get, put-patch, delete
        - 가입성공시(201) - SignInPage 이동( useNavigate ) 
        - 가입실패(4XX) - 현재페이지에 에러메시지를 출력 
        */
       
        // json-server version
        await api.post('/users', data)
                .then( response => {
                    console.log(`debug >>>> axios request success` , response);  
                    if( response.status === 201) {
                        moveUrl('/users/signIn');
                    } 
                })
                .catch( error => {
                    console.log(`debug >>>> axios request error` , error); 
                }) 

    }


    return(
        <Container>
            <FormWrapper>
                <Title>회원가입</Title>
                <form onSubmit={signUpHandler}>
                    <Input  type='text' 
                            name='name'
                            placeholder="이름 입력하세요"
                            value={form.name} 
                            onChange={keyHandler}/>
                    <Input  type='email' 
                            name='email'
                            placeholder="이메일 입력하세요"
                            value={form.email} 
                            onChange={keyHandler}/>
                    <Input  type='password' 
                            name='password'
                            placeholder="패스워드 입력하세요"
                            value={form.passwrd} 
                            onChange={keyHandler}/>
                    <Button type='submit'>가입하기</Button>
                </form>
                <TextLink to='/users/signIn'>이미회원이시면 로그인</TextLink>
            </FormWrapper>
        </Container>
    )
}

export default SignUpPage ;

