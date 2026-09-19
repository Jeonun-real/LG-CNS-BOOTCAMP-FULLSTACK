import styled from "styled-components";

const ItemCard = styled.article`
    min-width: 0;
    padding: 16px;
    border: 1px solid #dbeafe;
    border-radius: 12px;
    background: #f8fbff;
`;

const CategoryCode = styled.span`
    display: inline-block;
    margin-bottom: 8px;
    padding: 3px 8px;
    border-radius: 999px;
    background: #dbeafe;
    color: #2563eb;
    font-size: 11px;
    font-weight: 700;
`;

const CategoryName = styled.h3`
    margin: 0 0 8px;
    color: #475569;
    font-size: 14px;
    font-weight: 600;
`;

const ForecastValue = styled.p`
    margin: 0;
    color: #0f172a;
    font-size: 24px;
    font-weight: 800;
    overflow-wrap: anywhere;
`;

const ForecastItem = ({ forecast }) => {
    if (!forecast) {
        return null;
    }

    return (
        <ItemCard>
            <CategoryCode>{forecast.category}</CategoryCode>
            <CategoryName>
                {forecast.categoryName || forecast.category}
            </CategoryName>
            <ForecastValue>{forecast.fcstValue}</ForecastValue>
        </ItemCard>
    );
};

export default ForecastItem;
