
import MaterialButton from "../components/material/MaterialButton";

const Buttonpage = () => {

    const saveHandler = () => {
        console.log('글작성 버튼 클릭');
    }

    const listHandler = () => { 
        console.log('글목록 버튼 클릭');
    }

    return (
        <div>
            <MaterialButton 
                title='글작성'
                onClick={(e) => saveHandler()}/>
            <MaterialButton 
                title='글목록'
                 onClick={(e) => listHandler()}/>
        </div>
    );

}

export default Buttonpage;