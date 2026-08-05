import { useState } from "react";
import Greeting     from "../../components/rendering/Greeting";
import LogoutButton from "../../components/rendering/LogoutButton";

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
                    ''
            }
        </div>
    )
}

export default RenderingPage ;