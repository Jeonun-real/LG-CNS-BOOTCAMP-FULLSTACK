import axios from 'axios';

const endPoint = process.env.REACT_APP_BACKEND_ENDPOINT;

const api = axios.create({
    baseURL: endPoint
});

// axios token 재밝습
// api.interceptors.response.use(
//             (response) => response, 
//             async (error) => {

//         console.log("debug >>>>>>>> response error : ", error);
//         console.log("debug >>>>>>>> response config : ", error.config);
//         console.log("debug >>>>>>>> response response : ", error.response);
//         const originalRequest = error.config;
//         if( error.response?.status === 401 && !originalRequest._retry) {
//             originalRequest._retry = true;
//             try {
//                 const refreshToken = localStorage.getItem("rt");
//                 console.log("debug >>>>>>>> refreshToken : ", refreshToken);
//                 const res = await axios.post('auth/renew', {}, {
//                                 baseURL: "http://localhost:8088/",
//                                 headers : {
//                                     Authorization : `Bearer ${refreshToken}`
//                                 }
//                             });
//                 console.log("debug >>>>>>>> ")
//                 }


export default api;