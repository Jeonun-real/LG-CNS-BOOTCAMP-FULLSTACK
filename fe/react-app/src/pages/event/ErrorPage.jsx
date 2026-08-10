import { Link } from "react-router-dom";

// state가 아닌 queryString 으로 전달되는 형식일 경우 
import { useSearchParams  } from "react-router-dom";

const ErrorPage = () => {

    const [searchParams] = useSearchParams();
    const category = searchParams.get('category'); // react
    const sort     = searchParams.get('sort'); // latest

    return (
        <div>
            <center>로그인 실패({category} , {sort})</center>
            &nbsp;&nbsp;&nbsp;
            <Link to="/">랜딩페이지로...</Link>
        </div>
    )
}

export default ErrorPage ;