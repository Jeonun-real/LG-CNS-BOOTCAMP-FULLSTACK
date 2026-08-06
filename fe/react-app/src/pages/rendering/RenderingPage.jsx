import { useState } from "react";
import Greeting     from "../../components/rendering/Greeting";
import LogoutButton from "../../components/rendering/LogoutButton";
import LoginButton from "../../components/rendering/LoginButton";

const RenderingPage = () => {
    
    // script 
    const [flag, setFlag] = useState(true); 

    // ui 
    return (
        <div>
            <Greeting flag={flag} /> 
            {
                flag ?
                    <LogoutButton   isLogin={setFlag}/>
                :
                    <LoginButton   isLogin={setFlag}/>

            }
        </div>
    )
}

export default RenderingPage ;