import { BrowserRouter, Route, Routes } from "react-router-dom";


///////////////////////////// router page 
import EventPage from "./pages/event/EventPage";
import SuccessPage from "./pages/event/SuccessPage";
import ErrorPage from "./pages/event/ErrorPage";
import ViewPage from "./pages/event/ViewPage";


const TestRouterApp= () => {
    return(
        <BrowserRouter>
            <Routes>

                <Route path="/" element={<EventPage />}/>
                <Route path="/success" element={<SuccessPage />}/>
                <Route path="/error" element={<ErrorPage />}/>
                <Route path="/read/:id" element={<ViewPage />}/>

            </Routes>
        </BrowserRouter>
    );
}

export default TestRouterApp;