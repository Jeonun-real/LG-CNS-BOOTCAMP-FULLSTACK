import { useState } from "react";
import styled from "styled-components";
import { Link, useNavigate } from "react-router-dom";
import api from '../../../api/axios';


const SigInPage = () => {

    const moveUrl = useNavigate();

    const [formData, setFormData] = useState({
        email: "",
        password: "",
    });

    const changeHandler = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({...prev, [name]: value}));
    };



    /*
    CRUD
    - axios: get(), post(), put() | patch(), delete();
    QueryString(url뒤에 직접 바인딩) -> router에서도 사용가능!!
    - api.get('url?email=xxx&password=xxxx')
    - api.get('url',{ 
                    params: {
                        email: form.email,
                        pad: form.password                               
                    }
    })
    DB: SQL(Structor Query Laguage)
    select hame, email, password
    from table
    where email=? and password = ?
    */


    const submitHandler = async (e) => {
        e.preventDefault();
        console.log("로그인 정보:", formData);

        await api.get(`/users?email=${formData.email}&password=${formData.password}`)
            .then(response => {
                console.log('response',response);
                console.log('response',response.data);
                if(response.status === 200){
                    localStorage.setItem('user',response.data[0].email);
                    moveUrl('/blogs/index');
                    // header access token을 가져오고 싶을 수 있어야 함.
                    // 인증, 인가 -> JWT or spring security
                    
                }
            })
            .catch(err => {
                console.log(err);
            })
    }


    //     e.preventDefault();
    //     console.log("로그인 정보:", formData);

    //     const data = {...formData};

    //     await api.get(`/users/email=${formData.email}&password=${formData.password}`)
    //         .then(response => {
    //             console.log('response',response);
    //             if(response.status === 200){
    //                 localStorage.setItem('user'.reponse.data[0].name)
    //             }
    //         })
    //         .catch(err => {
    //             console.log(err);
    //         })
        
        
    //     }


    // };

    return (
        <PageContainer>
            <FormBox onSubmit={submitHandler}>
                <Title>로그인</Title>
                <InputGroup>
                <Label htmlFor="email">이메일</Label>
                <Input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={changeHandler}
                    placeholder="이메일을 입력하세요"
                    required
                />
                </InputGroup>

                <InputGroup>
                <Label htmlFor="password">패스워드</Label>
                <Input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={changeHandler}
                    placeholder="패스워드를 입력하세요"
                    required
                />
                </InputGroup>

                <SubmitButton type="submit">로그인</SubmitButton>
            </FormBox>
        </PageContainer>
    );
};

export default SigInPage;

const PageContainer = styled.div`
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f6f8;
`;

const FormBox = styled.form`
  width: 400px;
  padding: 40px;
  border-radius: 12px;
  background-color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
`;

const Title = styled.h2`
  margin: 0 0 30px;
  text-align: center;
`;

const InputGroup = styled.div`
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
`;

const Label = styled.label`
  margin-bottom: 8px;
  font-weight: bold;
`;

const Input = styled.input`
  padding: 12px;
  border: 1px solid #cccccc;
  border-radius: 6px;
  font-size: 16px;

  &:focus {
    outline: none;
    border-color: #4f7cff;
  }
`;

const SubmitButton = styled.button`
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 6px;
  background-color: #4f7cff;
  color: white;
  font-size: 16px;
  cursor: pointer;

  &:hover {
    background-color: #345fd1;
  }
`;