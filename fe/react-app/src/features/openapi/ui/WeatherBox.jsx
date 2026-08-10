import '../css/weather.css' ;

/*
Q) 
props 로 전달되는 weather 는 openweather 서버와 통신을 통한 해당 도시의 날씨정보 
weather - 현재위치, 섭씨(C), 날씨 요약정보 
섭씨 : (kelven - 273.15).toFixed(1)

*/
const WeatherBox = ({weather}) => {
    return(
        <div className='weather-box'>
            <div className='weather-city'>{weather.name}</div>
            <div className='weather-temp'>
            {
                weather?.main?.temp 
                ? (weather?.main?.temp- 273.15).toFixed(1)
                : "로딩중"
            }
            </div>
            <div className='weather-desc'>
            {
                weather?.weather?.[0]?.description
            }
            </div>
        </div>
    );
}

export default WeatherBox; 
