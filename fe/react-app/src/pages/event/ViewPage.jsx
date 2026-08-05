//path variable 형태의 데이터를 얻어올 때
import { useParams } from "react-router-dom";

const ViewPage = () => {
    const {id} = useParams();
    console.log('useparams :id',id);   
}

export default ViewPage;