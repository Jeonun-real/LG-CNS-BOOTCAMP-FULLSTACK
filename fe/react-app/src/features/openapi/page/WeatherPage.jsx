// import { useEffect, useState } from "react";
// import '../css/weather.css' ; 
// import WeatherBox from "../ui/WeatherBox";
// import WeatherButton from "../ui/WeatherButton";

// const WeatherPage = () => {

//     const key = process.env.REACT_APP_WEATHER_API_KEY ;
//     // console.log(`debug >>>> open api key ` , key); 
    
//     const cities = ["Seoul, KR", "Busan, KR", "Daejeon, KR", "Incheon, KR", "Paris", "New York"] ;

//     const [city, setCity] = useState('');
//     const [weather, setWeather] = useState({});

//     // handler 
//     const cityHandler = (e, city) => {
//         setCity(city)
//     }

//     ////////////////////////////// 도시버튼 이벤트 발생시 
//     // city update 호출되는 effect 
//     useEffect(() => {
//         console.log(`debug >>>> cityHandler city ${city}`);
//         if(city != '') {
//             getCityWeather();
//         }
//     }, [city]);

//     const getCityWeather = async () => {
        
//         const endPoint = `https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${key}` ; 
//         await fetch(endPoint)
//             .then( response => {
//                 console.log(`debug >>>> fetch response ` , response); 
//                 return response.json() ; 
//             })
//             .then( data => {
//                 console.log(`debug >>>> fetch response data ` , data);  
//                 setWeather(data); 
//             })
//             .catch( error => {
//                 console.log(`debug >>>> fetch error ` , error);  
//             }) ;
//     };

//     ///////////////////////// 마운트시 
//     const getCurrentLocation = () => {
//         navigator.geolocation.getCurrentPosition((position) => {
//             let lat = position.coords.latitude ; 
//             let lon = position.coords.longitude ;
//             getCurrentWeather(lat, lon);
//         });
//     }

//     /*
//     Q)
//     fetch api - get(QueryString)
//     endpoint - https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid=${key}
//     */
//     const getCurrentWeather = async (lat, lon) => {
//         console.log(`debug >>>> getCurrentWeather lat ${lat}`);
//         console.log(`debug >>>> getCurrentWeather lon ${lon}`);
//         const endPoint = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${key}` ; 
        
//         await fetch(endPoint)
//             .then( response => {
//                 console.log(`debug >>>> fetch response ` , response); 
//                 return response.json() ; 
//             })
//             .then( data => {
//                 console.log(`debug >>>> fetch response data ` , data);  
//                 // 원하는 코드 작업 
//             })
//             .catch( error => {
//                 console.log(`debug >>>> fetch error ` , error);  
//             }) ;

        
        
        
       
//     }

//     useEffect(() => {
//         getCurrentLocation();
//     }, [])
    

//     // UI Template 
//     return (
//         <div className="container">
//             <WeatherBox weather={weather} />
//             <WeatherButton 
//                             cities={cities}
//                             city={city}
//                             handler={cityHandler} /> 
//         </div>
//     );

// }


// export default WeatherPage ;







// kakao map version ?????

import { useEffect, useState }  from "react";
import WeatherBox               from "../ui/WeatherBox";
import WeatherButton            from "../ui/WeatherButton";
import KakaoMap                 from "../ui/KakaoMap";

import '../css/weather.css' ; 

const WeatherPage = () => {

    const key = process.env.REACT_APP_WEATHER_API_KEY ;
    // console.log(`debug >>>> open api key ` , key); 
    
    const cities = ["서울", "부산", "대전", "인천", "대구", "전남광주"] ;

    const [city, setCity] = useState('');
    const [weather, setWeather] = useState({});

    // handler 
    const cityHandler = (e, city) => {
        setCity(city)
        // 기존 api 대신 좌표기반으로 변경 
        getCoordsByCity(city) ;

    }

    ////////////////////////////// 도시버튼 이벤트 발생시 
    // city update 호출되는 effect 
    useEffect(() => {
        console.log(`debug >>>> cityHandler city ${city}`);
        if(city != '') {
            getCityWeather();
        }
    }, [city]);

    const getCityWeather = async () => {
        
        const endPoint = `https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${key}` ; 
        await fetch(endPoint)
            .then( response => {
                console.log(`debug >>>> fetch response ` , response); 
                return response.json() ; 
            })
            .then( data => {
                console.log(`debug >>>> fetch response data ` , data);  
                setWeather(data); 
            })
            .catch( error => {
                console.log(`debug >>>> fetch error ` , error);  
            }) ;
    };

    ///////////////////////// 마운트시 
    const getCurrentLocation = () => {
        navigator.geolocation.getCurrentPosition((position) => {
            let lat = position.coords.latitude ; 
            let lon = position.coords.longitude ;
            getCurrentWeather(lat, lon);
        });
    }

    /*
    Q)
    fetch api - get(QueryString)
    endpoint - https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid=${key}
    */
    const getCurrentWeather = async (lat, lon) => {
        console.log(`debug >>>> getCurrentWeather lat ${lat}`);
        console.log(`debug >>>> getCurrentWeather lon ${lon}`);
        const endPoint = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${key}` ; 
        
        await fetch(endPoint)
            .then( response => {
                console.log(`debug >>>> fetch response ` , response); 
                return response.json() ; 
            })
            .then( data => {
                console.log(`debug >>>> fetch response data ` , data);  
                // 원하는 코드 작업 
            })
            .catch( error => {
                console.log(`debug >>>> fetch error ` , error);  
            }) ;

        
        
        
       
    }

    useEffect(() => {
        getCurrentLocation();
    }, [])


    ///////////////// map
    const getWeatherByCoords = async (lat, lng) => {
        console.log(`debug >>>> getWeatherByCoords lat, lon : ${lat}, ${lng} `);
        let endPoint = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lng}&appid=${key}`;
        await fetch(endPoint)
            .then( response => {
                console.log(`debug >>>> fetch response ` , response); 
                return response.json() ; 
            })
            .then( data => {
                console.log(`debug >>>> fetch response data ` , data);  
                setWeather(data);
            })
            .catch( error => {
                console.log(`debug >>>> fetch error ` , error);  
            }) ;
    };

    //////////////// 도시 클릭시 좌표 얻기(kakao gecoder)
    const [moveTo, setMoveTo] = useState({});
    ///////////////// 좌표 변환 함수
    const getCoordsByCity = (cityName) => {
        console.log(`debug >>>> getCoordsByCity city name  ` , cityName);  
        const geocoder = new window.kakao.maps.services.Geocoder();
        geocoder.addressSearch(cityName, (result, status, { analyze_type: SIMILAR} ) => {
            console.log(`debug >>>> getCoordsByCity status  ` , status);  
            console.log(`debug >>>> getCoordsByCity status ok  ` , window.kakao.maps.services.Status.OK);  
            console.log(`debug >>>> getCoordsByCity result  ` , result);  
            if(status === window.kakao.maps.services.Status.OK) {
                const lat = parseFloat(result[0].y) ;
                const lng = parseFloat(result[0].x) ;
                setMoveTo({lat : lat , lng : lng , time : Date.now() });
                getWeatherByCoords(lat, lng); 
            } else {
                console.log(`debug >>>>  getCoordsByCity 좌표변환 실패`);  
            }
        });

    }

    // UI Template 
    return (
        <div className="container">
            {/* kakao map version add : coords(좌표-위(lat)/경도(lon)) */}
            <KakaoMap   setWeatherByCoords={getWeatherByCoords}
                        moveTo={moveTo} />

            {/* api.openweathermap.org */}
            <WeatherBox weather={weather} />
            <WeatherButton 
                            cities={cities}
                            city={city}
                            handler={cityHandler} /> 
        </div>
    );

}


export default WeatherPage ;







