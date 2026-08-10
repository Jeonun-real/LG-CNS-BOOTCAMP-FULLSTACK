import { Link }         from "react-router-dom";


// 데이터를 state 통해서 전달받을 경우 
import { useLocation }  from "react-router-dom";

const SuccessPage = () => {

    // state 이용한 데이터 공유
    const location = useLocation();
    const {user, from} = location.state || {} ; 

    // scope 차이가 있음...
    // 스크립트 객체를 이용한 데이터 공유로 router와 무관함.
    const name = localStorage.getItem('userName');    

    return (
        <div>
            <center>{name}-{user.name}님 로그인 성공</center>
            &nbsp;&nbsp;&nbsp;
            <Link to="/read/10">상세페이지로...</Link>
            &nbsp;&nbsp;&nbsp;
            <Link to="/">랜딩페이지로...</Link>
        </div>
    )
}

export default SuccessPage ;