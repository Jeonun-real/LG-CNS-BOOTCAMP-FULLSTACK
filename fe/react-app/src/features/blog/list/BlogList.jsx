import styled from "styled-components";
import BlogItem from "../item/BlogItem";

const BlogList = (props) => {
    return(
        <Wrapper>
            {
                props.ary?.map((blog, idx) => {
                    return <BlogItem key={idx} blog={blog}/>
                })

            }
        </Wrapper>
    )}

export default BlogList;


const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  margin-top: 16px;

  & > *:not(:last-child) {
    margin-bottom: 16px;
  }
`;