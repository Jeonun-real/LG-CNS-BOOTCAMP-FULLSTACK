import styled from "styled-components";
import Button from "../../../components/styled/Button";

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

const CommentText = styled.p`
    font-size: 16px;
    white-space : pre-wrap ; 
`;


const BlogCommentItem = ({comment, handler}) => {
    // 로그인사용자 이메일
    const user = localStorage.getItem('user');
    return(
        <Wrapper>
            <CommentText>{comment.comment}</CommentText>
            {
                user === comment.email &&
                    <div>
                        <Button title='삭제'
                                onClick={(e) => handler(e, comment.id)} /> 
                    </div>
            }
        </Wrapper>
    );
}

export default BlogCommentItem ;