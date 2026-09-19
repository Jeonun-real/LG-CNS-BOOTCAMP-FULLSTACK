import { BrowserRouter, Route, Routes } from "react-router-dom";


import BlogIndexPage    from "./features/blog/page/BlogIndexPage";
import BlogReadPage     from "./features/blog/page/BlogReadPage";
import BlogWritePage    from "./features/blog/page/BlogWritePage";
import SignInPage       from "./features/user/page/SignInPage";
import SignUpPage       from "./features/user/page/SignUpPage";
import WeatherPage       from "./features/openapi/page/WeatherPage";
import ForecastPage       from "./features/forecast/page/ForecastPage";



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

                
                {/* blog - comment */}
                <Route path="/blogs/read/:blogId"   element={ <BlogReadPage />} />

                {/* open api */}
                <Route path="/openapi/index"        element={ <WeatherPage />} />


                {/* forecast api */}
                <Route path="/forecast/index"        element={ <ForecastPage />} />


            </Routes>
        </BrowserRouter>
    )
}

export default ToyApp ;