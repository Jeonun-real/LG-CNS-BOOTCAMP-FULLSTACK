import Button from "../styled/Button";

const LoginButton = (props) => {
    const loginHandler = (setFlag) => {
        setFlag(true) ;
    }
    return (
        <div>
            <Button title={`로그인`}
                    onClick={() => loginHandler(props.isLogin)}/>
        </div>
    )
}
export default LoginButton ;