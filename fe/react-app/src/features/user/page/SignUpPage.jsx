import { useState } from "react";
import styled from "styled-components";
import { Link, useNavigate } from "react-router-dom";
import api from '../../../api/axios';




const SignUpPage = () => {
    const moveUrl = useNavigate();
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
    });

    const changeHandler = (e) => {
        const { name, value } = e.target;

        setFormData((prev) => ({...prev, [name]: value}));
    };

    const submitHandler = async (e) => {
        e.preventDefault();

        console.log("회원가입 정보:", formData);

        const data ={...formData};


        //json-server 방식 통신
        await api.post('/users',data)
            .then(response => {
                console.log(response);
                if(response.status===201){
                    moveUrl('/users/signIn');
                }
                else{
                    console.log('debug');
                }

            })
            .catch(err => {
                console.log(err);
            })


        }


    return (
        <PageContainer>
            <FormBox onSubmit={submitHandler}>
                <Title>회원가입</Title>

                <InputGroup>
                <Label htmlFor="name">이름</Label>
                <Input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name} 
                    onChange={changeHandler}
                    placeholder="이름을 입력하세요"
                    required
                />
                </InputGroup>

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

                <SubmitButton type="submit">회원가입</SubmitButton>
                <LoginGuide>
                    이미 회원가입하셨나요?
                    <LoginLink to="/users/signIn">
                    로그인
                    </LoginLink>
                </LoginGuide>
            </FormBox>
        </PageContainer>
    );
};

export default SignUpPage;

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

const LoginGuide = styled.p`
  margin-top: 20px;
  margin-bottom: 0;
  text-align: center;
  font-size: 14px;
  color: #666666;
`;

const LoginLink = styled(Link)`
  margin-left: 6px;
  color: #4f7cff;
  font-weight: bold;
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
`;