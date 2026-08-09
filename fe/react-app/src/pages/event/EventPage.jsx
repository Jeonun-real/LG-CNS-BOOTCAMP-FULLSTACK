import Button from 'react-bootstrap/Button' ;
import 'bootstrap/dist/css/bootstrap.min.css' ;
import { useEffect, useState } from 'react';
import api from '../../api/axios';
/////////////////////////////////////////////////////
import { useNavigate } from 'react-router-dom';



// npm install react-bootstrap bootstrap 
const EventPage = () => {
    /*
    변수?
    - scope : 전역, 지역
    - state 
    const data = {
        id : 'jslim' , password : 'jslim' 
    }
    상태관리 안되는 변수사용
    let id = data.id ;
    let password = data.password ;

    상태관리 
    const [id , setId] = useState();
    const [password , setPassword] = useState() ;
    setId(data.id), setPassword(data.password);
    */

    // 사용자의 입력값과 스크립트의 변수를 동기화를 위한 상태관리 
    const [email, setEmail] = useState('');
    const [pswd, setPswd]   = useState('');

    // const emailHandler = (e) => {
    //     setEmail(e.target.value) ;
    // }
    // const passwordHandler = (e) => {
    //     setPswd(e.target.value) ;
    // }

    // transition 위한 HOOK 
    const moveUrl = useNavigate();


    const signInHandler = async (e, email, pswd) => {
        e.preventDefault() ;

        await api.get(`/users?email=${email}&pswd=${pswd}`)
                .then( response => {
                    console.log(`debug >>>> response : ` , response ); 
                    const ary = response.data;
                    if(ary.length > 0 ) {
                        // 인증된 사용자 정보 관리
                        // sessionStorage, localStorage 
                        // 인증 - 신원확인 , 인가 - 특정 url 접근할 수 있는 권한 
                        // Json Web Token(JWT) - token (header) 
                        // response.headers.get('Authorization'); 
                        // localStorage.setItem('token', 'token-xxxxxxxxxx') ; 
                        // react component transition 
                        const user = ary[0] ; 
                        localStorage.setItem('userName', user.name) ; 
                        moveUrl('/success' , {
                            state : {
                                user , 
                                from : '/signIn' 
                            }
                        });
                    } else {
                        moveUrl('/error?category=react&sort=latest');
                        
                    }

                })
                .catch( err => {
                    console.log(`debug >>>> error : ` , err ); 
                });
    }

    useEffect(() => {
        console.log(`debug >>>> emailHandler email : ` , email ); 
        console.log(`debug >>>> emailHandler password : ` , pswd ); 
    });

    return(
        <div className='container'>
            
            <div className="mb-3 mt-3">
                <label  for="email" className="form-label">Email:</label>
                <input  type="email" 
                        className="form-control" 
                        id="email" 
                        placeholder="Enter email" 
                        name="email" 
                        value={email}
                        onChange={ (e) => {
                            setEmail(e.target.value) 
                        }}/>
            </div>
            <div class="mb-3">
                <label  for="pwd" className="form-label">Password:</label>
                <input  type="password" 
                        className="form-control" 
                        id="pwd" 
                        placeholder="Enter password" 
                        name="pswd" 
                        value={pswd}
                        onChange={ (e) => {
                            setPswd(e.target.value) 
                        }}/>
            </div>
            <div class="form-check mb-3">
                <label  className="form-check-label">
                <input  className="form-check-input" 
                        type="checkbox" 
                        name="remember" /> Remember me
                </label>
            </div>
            <Button variant='primary'
                    onClick={ (e) => signInHandler(e, email, pswd) }>SignIn</Button>
            
        </div>
    );

}

export default EventPage ;

