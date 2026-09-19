import { useEffect, useRef } from "react";

const KakaoMap = ({ setWeatherByCoords, moveTo} ) => {

    const mapRef      = useRef(null);
    const markerRef   = useRef(null);

    useEffect(() => {
        if (!window.kakao?.maps) {
            console.error("카카오맵 SDK가 로드되지 않았습니다.");
            return;
        }

        window.kakao.maps.load(() => {
            const container = document.getElementById("map");
            const centerPosition = new window.kakao.maps.LatLng(
                37.5665,
                126.9780
            );

            const map = new window.kakao.maps.Map(container, {
                center: centerPosition,
                level: 3
            });

            const marker = new window.kakao.maps.Marker({
                position: centerPosition
            });

            marker.setMap(map);
            mapRef.current = map;
            markerRef.current = marker;

            window.kakao.maps.event.addListener(
                map,
                "click",
                function (mouseEvent) {
                const lat = mouseEvent.latLng.getLat();
                const lng = mouseEvent.latLng.getLng();

                marker.setPosition(mouseEvent.latLng);
                setWeatherByCoords?.(lat, lng);
                }
            );
        });

    }, []);


    // 외부에서 좌표 들어오면 지도 이동
    useEffect(() => {
        if (!moveTo?.lat || !moveTo?.lng) return;
        if (!mapRef.current || !markerRef.current) return;

        const lat = parseFloat(moveTo.lat);
        const lng = parseFloat(moveTo.lng);

        const position = new window.kakao.maps.LatLng(lat, lng);

        console.log("지도 이동:", lat, lng);

        mapRef.current.setCenter(position);
        markerRef.current.setPosition(position);
    }, [moveTo]);

    return(
        <div id='map' style={ { width: '100%' , height : '400px'} }></div>
    );
}

export default KakaoMap; 
