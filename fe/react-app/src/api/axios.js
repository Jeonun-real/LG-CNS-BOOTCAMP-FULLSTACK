import axios from 'axios';

const endPoint = process.env.REACT_APP_BACKEND_ENDPOINT;
const api = axios.create({
    baseURL: endPoint
});

export default api;