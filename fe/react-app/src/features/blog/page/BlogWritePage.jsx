import styled from "styled-components";
import Button from "../../../components/styled/Button";
import TextInput from "../../../components/styled/TextInput";
import { useNavigate } from "react-router-dom";

const Wrapper = styled.div`
    padding: 16px;
    width: calc(100% - 32px);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
`;

const Container = styled.div`
    width: 100%;
    max-width: 720px;

    & > *:not(:last-child) {
        margin-bottom: 16px;
    }
`;

const WelcomeMessage = styled.div`
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 16px;
    color: #333;
`;

const CategoryLabel = styled.div`
    font-size: 13px;
    font-weight: 600;
    color: #6b7280;
    margin-bottom: 8px;
`;

const CategoryWrapper = styled.div``;

const CategoryRow = styled.div`
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    width: 100%;
`;

const CategoryChip = styled.button`
    box-sizing: border-box;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    white-space: nowrap;

    height: 36px;
    line-height: 1;

    border: 1.5px solid
        ${(props) => (props.$active ? "#6366f1" : "#e5e7eb")};
    background: ${(props) => (props.$active ? "#6366f1" : "#ffffff")};
    color: ${(props) => (props.$active ? "#ffffff" : "#4b5563")};

    font-size: 13px;
    font-weight: 600;
    padding: 0 18px;
    border-radius: 999px;
    cursor: pointer;
    transition: all 0.15s;
    outline: none;

    &:focus {
        outline: none;
        box-shadow: none;
    }

    &:hover {
        border-color: #6366f1;
    }
`;

/* 버튼을 같은 줄에 배치 */
const ButtonRow = styled.div`
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 12px;
    width: 100%;
`;

const BlogWritePage = () => {
    const user = localStorage.getItem("user");
    const CATEGORIES = ["개발", "생활", "취미", "일상"];

    const moveUrl = useNavigate();

    return (
        <Wrapper>
            <Container>
                {user && (
                    <WelcomeMessage>
                        {user}님 환영합니다.
                    </WelcomeMessage>
                )}

                {/* 카테고리 선택 */}
                <CategoryWrapper>
                    <CategoryLabel>카테고리</CategoryLabel>

                    <CategoryRow>
                        {CATEGORIES.map((category) => (
                            <CategoryChip
                                key={category}
                                type="button"
                            >
                                {category}
                            </CategoryChip>
                        ))}
                    </CategoryRow>
                </CategoryWrapper>

                {/* 제목 */}
                <TextInput height={20} />

                {/* 내용 */}
                <TextInput height={280} />

                {/* 버튼 */}
                <ButtonRow>
                    <Button
                        title="글 작성하기"
                        onClick={() => {
                            console.log("글 작성");
                        }}
                    />

                    <Button
                        title="이전"
                        onClick={() => {
                            moveUrl("/blogs/index");
                        }}
                    />
                </ButtonRow>
            </Container>
        </Wrapper>
    );
};

export default BlogWritePage;