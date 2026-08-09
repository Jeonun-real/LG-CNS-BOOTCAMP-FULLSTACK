import styled from "styled-components";
import BlogCommentItem from "../item/BlogCommentItem";

const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: center;
    margin-top: 16px;
    & > * {
        :not(:last-child) {
            margin-bottom: 16px;
        }
    }
    gap: 16px;
`;

const BlogCommentList = ({comments, handler}) => {
    
    return(
        <Wrapper>
            {
                comments.map( (comment, idx) => {
                    return <BlogCommentItem     
                                key={idx}
                                comment={comment}
                                handler={handler} />
                })
            }
        </Wrapper>
    );

}

export default BlogCommentList ;
