import styled       from "styled-components";
import BlogItem     from "../item/BlogItem";


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

const BlogList = (props) => {
    return(
        <Wrapper>
            {   props.ary && props.ary.length > 0 ?
                props.ary.map((blog, idx) => {
                    return <BlogItem    key={idx}
                                        blog={blog} />
                })
                :
                '등록된 글이 없습니다.'
            }
        </Wrapper>
    )
}

export default BlogList ;
