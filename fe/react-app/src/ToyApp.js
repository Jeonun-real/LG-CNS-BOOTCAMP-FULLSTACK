import { BrowserRouter, Route, Routes } from "react-router-dom";


///////////////////////////// router page 
import SignUpPage from "./features/user/page/SignUpPage";
import SignInPage from "./features/user/page/SignInPage";

import BlogIndexPage from "./features/blog/page/BlogIndexPage";
import BlogWritePage from "./features/blog/page/BlogWritePage";
import BlogReadPage from "./features/blog/page/BlogReadPage";


const TestRouterApp= () => {
    return(
        <BrowserRouter>
            <Routes>

                {/* user */}
                <Route path="/" element={<SignUpPage />}/>
                <Route path="/users/signIn" element={<SignInPage />}/>
                {/*  blog */}
                <Route path="/blogs/index" element={<BlogIndexPage />}/>
                <Route path="/blogs/write" element={<BlogWritePage />}/>
                <Route path="/blogs/read/:blogId" element={<BlogReadPage />}/>
                {/* user */}

            </Routes>
        </BrowserRouter>
    );
}

export default TestRouterApp;