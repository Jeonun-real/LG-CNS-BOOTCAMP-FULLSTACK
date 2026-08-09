import { CategoryRounded } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";

const Wrapper = styled.div`
    width: calc(100% - 32px);
    padding: 16px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: center;
    border: 1px solid grey;
    border-radius: 8px;
    cursor: pointer;
    background: white;
    :hover {
        background: lightgrey;
    }
`;

const TitleText = styled.p`
    font-size: 20px;
    font-weight: bold ;
`;

const CategoryBadge = styled.span`
    display: inline-flex;
    align-items: center;
    height: 24px;
    padding: 0 10px;
    margin-bottom: 8px;
    border-radius: 999px;
    background: #eef2ff;
    color: #6366f1;
    font-size: 12px;
    font-weight: 600;
    line-height: 1;
`;

const BlogItem = ({blog}) => {

    const moveUrl = useNavigate();
    return(
        <Wrapper
            onClick={() => {
                moveUrl(`/blogs/read/${blog.id}`);
            }}>
            {blog.category && <CategoryBadge>{blog.category}</CategoryBadge>}
            <TitleText>{blog.title}</TitleText>
        </Wrapper>
    )
}

export default BlogItem ;
