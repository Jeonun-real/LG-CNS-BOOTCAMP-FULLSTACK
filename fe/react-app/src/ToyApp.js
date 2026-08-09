import { BrowserRouter, Route, Routes } from "react-router-dom";


import BlogIndexPage    from "./features/blog/page/BlogIndexPage";
import BlogReadPage     from "./features/blog/page/BlogReadPage";
import BlogWritePage    from "./features/blog/page/BlogWritePage";
import SignInPage       from "./features/user/page/SignInPage";
import SignUpPage       from "./features/user/page/SignUpPage";



const ToyApp = () => {
    return(
        <BrowserRouter>
            <Routes>
                {/* user */}
                <Route path="/"                     element={ <SignUpPage />} />
                <Route path="/users/signIn"         element={ <SignInPage />} />

                {/* blog */}
                <Route path="/blogs/index"          element={ <BlogIndexPage />} />
                <Route path="/blogs/write"          element={ <BlogWritePage />} />
                <Route path="/blogs/read/:blogId"   element={ <BlogReadPage />} />
                
                {/* blog - comment */}

            </Routes>
        </BrowserRouter>
    )
}

export default ToyApp ;