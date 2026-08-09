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

const SignInPage = () => {

    // state 
    const [form, setForm] = useState({
        email : '', password : ''
    });
    const moveUrl = useNavigate();

    // 기존값을 유지하면서 현재 입력된 필드에 대한 상태변화(업데이트)를 처리 
    const keyHandler = (e) => {
        const {name, value} = e.target ;
        setForm({...form, [name]:value});
    };
    
    /*
    CRUD
    - axios : get(), post(), put() | patch(), delete();
    QueryString(url 뒤에 직접 바인딩) -> router에서도 사용가능 확인!!
    - api.get('url?email=xxxx&password=xxxxx');
    - api.get('url' , {
        params : {
            email : form.email,
            password : form.password
        }
    })
    DB : SQL(Structor Query Language) 
    select  name, email, password 
    from    table
    where   email = ? and password = ? 
    */
    const signInHandler = async (e) => {
        e.preventDefault() ;

        // json-server version
        await api.get(`/users?email=${form.email}&password=${form.password}`)
                .then( response => {
                    console.log(`debug >>>> axios request success` , response);  
                    if(response.status === 200) {
                        
                        localStorage.setItem('user' , response.data[0].email) ;
                        // 추후 추가작업
                        // header access token 가져오고 싶을 수 있어야함.
                        // 인증, 인가 -> JWT or spring security

                        moveUrl('/blogs/index') ;
                    }

                })
                .catch( error => {
                    console.log(`debug >>>> axios request error` , error); 
                });
        
        // spring version
    }

    return(
        <Container>
            <FormWrapper>
                <Title>SignIn</Title>
                <form onSubmit={signInHandler}>
                    <Input  type='email' 
                            name='email'
                            placeholder="이메일 입력하세요"
                            value={form.email}
                            onChange={keyHandler}/>
                    <Input  type='password' 
                            name='password'
                            placeholder="패스워드 입력하세요"
                            value={form.password}
                            onChange={keyHandler}/>
                    <Button type='submit'>SignIn</Button>
                </form>
                <TextLink to='/'>회원가입</TextLink>
            </FormWrapper>
        </Container>
    )
}

export default SignInPage ;

