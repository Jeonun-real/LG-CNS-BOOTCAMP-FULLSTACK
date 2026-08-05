import GuestGreeting    from "./GuestGreeting";
import UserGreeting     from "./UserGreeting";


const Greeting = (props) => {
    {
        return props.flag ? <UserGreeting /> : <GuestGreeting />
    }
}

export default Greeting ;
