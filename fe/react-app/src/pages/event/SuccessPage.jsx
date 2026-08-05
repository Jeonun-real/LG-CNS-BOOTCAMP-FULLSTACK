import { Link } from 'react-router-dom';

//데이터를 state를 통해 전달받을 경우
import { useLocation } from 'react-router-dom';

const SuccessPage = () => {

    const location = useLocation();

    //캡슐화 방식
    const {user,from} = location.state || {} ;

    //로컬스토리지 방식
    const name = localStorage.getItem('userName');

    return(
        <div>
            <center>{name}-{user.name}</center>
            {/* <center>{user.name}님 로그인 성공</center> */}
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <Link to="/read/10">상세페이지로..</Link>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <Link to="/">랜딩페이지로..</Link>

        </div>
    );
}

export default SuccessPage;