import { useState } from "react";
import api from "../../../api/axios";
import styled from "styled-components";
import ForecastList from "../list/ForecastList";
import KakaoMap from "../../openapi/ui/KakaoMap";
import { BEACHES } from "../data/beaches";

const toInputDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
};

const today = new Date();
const threeDayStart = new Date(today);
threeDayStart.setDate(today.getDate() - 2);

const MIN_DATE = toInputDate(threeDayStart);
const MAX_DATE = toInputDate(today);

const FORECAST_TIMES = [
    "0200", "0500", "0800", "1100",
    "1400", "1700", "2000", "2300"
];

const Wrapper = styled.div`
    min-height: 100vh;
    background: linear-gradient(135deg, #e0f2fe 0%, #f5f7fb 60%);
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
`;

const Card = styled.div`
    width: 100%;
    max-width: 920px;
    background: white;
    padding: 40px 36px;
    border-radius: 20px;
    box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
`;

const IconCircle = styled.div`
    width: 56px;
    height: 56px;
    margin: 0 auto 16px;
    border-radius: 50%;
    background: #e8f1fd;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26px;
`;

const Title = styled.h2`
    text-align: center;
    margin-bottom: 6px;
    color: #1e293b;
    font-size: 22px;
`;

const Subtitle = styled.p`
    text-align: center;
    margin-bottom: 28px;
    color: #94a3b8;
    font-size: 13px;
`;

const Field = styled.div`
    margin-bottom: 18px;
`;

const FieldGrid = styled.div`
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 14px;

    @media (max-width: 720px) {
        grid-template-columns: 1fr;
    }
`;

const Label = styled.label`
    display: block;
    margin-bottom: 6px;
    font-weight: 600;
    font-size: 13px;
    color: #475569;
`;

const Input = styled.input`
    width: 100%;
    box-sizing: border-box;
    padding: 12px 14px;
    border: 1px solid ${(props) => (props.$error ? "#ef4444" : "#e2e8f0")};
    border-radius: 10px;
    font-size: 14px;
    outline: none;
    transition: 0.2s;
    background: #f8fafc;

    &:focus {
        border-color: #4a90e2;
        background: white;
        box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.15);
    }

    &::placeholder {
        color: #cbd5e1;
    }
`;

const Select = styled.select`
    width: 100%;
    box-sizing: border-box;
    padding: 12px 14px;
    border: 1px solid ${(props) => (props.$error ? "#ef4444" : "#e2e8f0")};
    border-radius: 10px;
    font-size: 14px;
    outline: none;
    background: #f8fafc;

    &:focus {
        border-color: #4a90e2;
        background: white;
        box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.15);
    }
`;

const MapSection = styled.section`
    margin-top: 22px;
    overflow: hidden;
    border: 1px solid #dbeafe;
    border-radius: 14px;
`;

const ErrorText = styled.span`
    display: block;
    margin-top: 4px;
    font-size: 12px;
    color: #ef4444;
`;

const Button = styled.button`
    width: 100%;
    padding: 14px;
    margin-top: 8px;
    background: ${(props) => (props.disabled ? "#a8c8ef" : "#4a90e2")};
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 15px;
    font-weight: 700;
    cursor: ${(props) => (props.disabled ? "not-allowed" : "pointer")};
    transition: 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;

    &:hover {
        background: ${(props) => (props.disabled ? "#a8c8ef" : "#357abd")};
    }

    &:active {
        transform: ${(props) => (props.disabled ? "none" : "scale(0.98)")};
    }
`;

const Spinner = styled.span`
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255, 255, 255, 0.4);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }
`;

const ServerError = styled.div`
    margin-top: 16px;
    padding: 10px 14px;
    background: #fef2f2;
    border: 1px solid #fecaca;
    color: #b91c1c;
    font-size: 13px;
    border-radius: 8px;
    text-align: center;
`;



const ForcastPage = () => {
    const [base_time, setBase_time] = useState("");
    const [base_date, setBase_date] = useState(MAX_DATE);
    const [beach_num, setBeach_num] = useState("");
    const [forecastList, setForecastList] = useState([]);

    const [errors,           setErrors] = useState({});
    const [loading,         setLoading] = useState(false);
    const [serverError, setServerError] = useState("");

    const selectedBeach = BEACHES.find(
        (beach) => beach.id === beach_num
    );

    const validate = () => {
        const nextErrors = {};

        if (!FORECAST_TIMES.includes(base_time)) {
            nextErrors.base_time = "예보 시간을 선택해주세요.";
        }

        if (!base_date || base_date < MIN_DATE || base_date > MAX_DATE) {
            nextErrors.base_date = "오늘부터 이전 3일 이내의 날짜를 선택해주세요.";
        }

        if (!beach_num.trim()) {
            nextErrors.beach_num = "해변 번호를 입력해주세요.";
        }

        setErrors(nextErrors);
        return Object.keys(nextErrors).length === 0;
    };

    const forcastHandler = async () => {
        if (!validate()) {
            return;
        }

        setLoading(true);
        setServerError("");

        const requestData = {
            beach_num: beach_num.trim(),
            base_date: base_date.replaceAll("-", ""),
            base_time: base_time.trim()
        };

        console.log("debug >>>> forecast request URL", "/forecast/fcst");
        console.log("debug >>>> forecast request data", requestData);
        console.log(
            "debug >>>> forecast request JSON",
            JSON.stringify(requestData)
        );

        try {
            const response = await api.post("/forecast/fcst", requestData);

            console.log("debug >>>> forecast response status", response.status);
            console.log("debug >>>> forecast response", response.data);
            const responseList = Array.isArray(response.data)
                ? response.data
                : [];

            setForecastList(responseList);

            if (responseList.length === 0) {
                setServerError("해당 조건에 맞는 예보 정보가 없습니다.");
            }
        } catch (error) {
            console.error("debug >>>> forecast request error", error);
            console.error(
                "debug >>>> forecast error status",
                error.response?.status
            );
            console.error(
                "debug >>>> forecast error response",
                error.response?.data
            );
            setForecastList([]);
            setServerError(
                error.response?.data?.message ||
                "예보 정보를 불러오지 못했습니다."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <Wrapper>
            <Card>
                <IconCircle>🌊</IconCircle>
                <Title>해수욕장 예보 조회</Title>
                <Subtitle>날짜, 시간, 해변 정보를 입력하고 예보를 확인하세요</Subtitle>

                <FieldGrid>
                    <Field>
                        <Label>해수욕장 이름</Label>
                        <Select
                            value={beach_num}
                            $error={!!errors.beach_num}
                            onChange={(e) => setBeach_num(e.target.value)}
                        >
                            <option value="">해수욕장을 선택하세요</option>
                            {BEACHES.map((beach) => (
                                <option key={beach.id} value={beach.id}>
                                    {beach.name}
                                </option>
                            ))}
                        </Select>
                        {errors.beach_num && <ErrorText>{errors.beach_num}</ErrorText>}
                    </Field>

                    <Field>
                        <Label>예보 날짜</Label>
                        <Input
                            type="date"
                            min={MIN_DATE}
                            max={MAX_DATE}
                            value={base_date}
                            $error={!!errors.base_date}
                            onChange={(e) => setBase_date(e.target.value)}
                        />
                        {errors.base_date && <ErrorText>{errors.base_date}</ErrorText>}
                    </Field>

                    <Field>
                        <Label>발표 시간</Label>
                        <Select
                            value={base_time}
                            $error={!!errors.base_time}
                            onChange={(e) => setBase_time(e.target.value)}
                        >
                            <option value="">시간을 선택하세요</option>
                            {FORECAST_TIMES.map((time) => (
                                <option key={time} value={time}>
                                    {time.slice(0, 2)}:{time.slice(2)}
                                </option>
                            ))}
                        </Select>
                        {errors.base_time && <ErrorText>{errors.base_time}</ErrorText>}
                    </Field>
                </FieldGrid>

                <MapSection>
                    <KakaoMap
                        moveTo={selectedBeach
                            ? {
                                lat: selectedBeach.lat,
                                lng: selectedBeach.lng
                            }
                            : null}
                    />
                </MapSection>

                <Button onClick={forcastHandler} disabled={loading}>
                    {loading ? (
                        <>
                            <Spinner /> 조회 중...
                        </>
                    ) : (
                        <>🌊 예보 정보 조회</>
                    )}
                </Button>

                {serverError && <ServerError>{serverError}</ServerError>}
                <ForecastList forecastList={forecastList} />
            </Card>
        </Wrapper>
    );
}

export default ForcastPage ; 
