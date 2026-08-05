import Button from 'react-bootstrap/Button';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useEffect, useState } from 'react';
import api from '../../api/axios';
import { useNavigate } from 'react-router-dom';


const EventPage = () =>{
    /*
    변수?
    - scope: 전역, 지역
    - state 상태관리
    
    const data = {
        id : 'sfasdf' , password ; 'safasdf'
    }

    let id = data.id (x)

    const [id, setId] = useState();
    cosnt [password, setPassword] =useState();
    setId(data.id), serPassword(data.password)

    */

    const [email,setEmail] = useState('');
    const [pswd, setPswd] = useState('');
    
    // const emailHandler = (e) =>{      
    //     setEmail(e.target.value);
    // }
    
    // const passwordHandler = (e) =>{      
    //     setPswd(e.target.value);
    // }

    //transition을 위한 hook
    const moveUrl = useNavigate();



    const signInHandler = async (e,email,pswd)=>{
        e.preventDefault();

        await api.get(`/users?email=${email}&pswd=${pswd}`)
        .then(response => {
            console.log('response',response);
            const ary = response.data;
            if(ary.length > 0){
                    //인증된 사용자 정보관리
                    //session storage, lcoastorage(front end 에서 공유)
                    //인증은 신원확인, 인가-특정 url에 접근가능한 권한
                    //인증이되면 json-web-token (JWT) - token(header)
                    //respose.headers.get('Autorization'); 으로 가져옴 
                    //localstorage.setItem('token',)
                    //react component transition
                const user = ary[0]; 
                localStorage.setItem('userName',user.name);

                //패스방식 이외에 데이터 전달 가능
                moveUrl('/success',{
                    state:{
                        user,
                        from:'/signIn'
                    }
                });
            } else {
                //get방식
                moveUrl('/error?category=react&sort=latest');
            }

        })
        .catch(err=>{
            console.log('debug',err);
        })


    }


    
    useEffect(()=>{
        console.log('emailhandler email',email);
        console.log('pswdhandler pswd',pswd);
    }); 

    return(
        <div className='container'>

            <div className="mb-3 mt-3">
                <label  htmlFor="email" className="form-label">Email:</label>
                <input  type="email" 
                        className="form-control" 
                        id="email" 
                        placeholder="Enter email" 
                        name="email" 
                        value ={email}
                        onChange={(e)=>setEmail(e.target.value)}/>
            </div>
            <div className="mb-3">
                <label  htmlFor="pwd" className="form-label">Password:</label>
                <input  type="password" 
                        className="form-control" 
                        id="pwd" 
                        placeholder="Enter password" 
                        name="pswd" 
                        value ={pswd}
                        onChange={(e)=>setPswd(e.target.value)}/>
            </div>
            <div className="form-check mb-3">
                <label  className="form-check-label">
                <input  className="form-check-input" 
                        type="checkbox" 
                        name="remember" /> Remember me
                </label>
            </div>
            <Button variant='primary' onClick={(e)=>signInHandler(e, email,pswd)}>signIn</Button>
        </div>
    );


}

export default EventPage;