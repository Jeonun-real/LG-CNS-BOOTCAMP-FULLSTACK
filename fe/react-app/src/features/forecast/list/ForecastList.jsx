import styled from "styled-components";
import ForecastItem from "../item/ForecastItem";

const Wrapper = styled.section`
    width: 100%;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
    margin-top: 24px;

    @media (max-width: 520px) {
        grid-template-columns: 1fr;
    }
`;

const EmptyMessage = styled.p`
    width: 100%;
    margin: 24px 0 0;
    padding: 18px;
    box-sizing: border-box;
    border-radius: 10px;
    background: #f8fafc;
    color: #64748b;
    font-size: 14px;
    text-align: center;
`;

const ForecastList = ({ forecastList = [] }) => {
    if (forecastList.length === 0) {
        return <EmptyMessage>조회된 예보 정보가 없습니다.</EmptyMessage>;
    }

    return (
        <Wrapper>
            {forecastList.map((forecast, index) => (
                <ForecastItem
                    key={`${forecast.category}-${index}`}
                    forecast={forecast}
                />
            ))}
        </Wrapper>
    );
};

export default ForecastList;
