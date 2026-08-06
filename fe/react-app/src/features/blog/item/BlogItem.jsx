import styled from "styled-components";


const BlogItem = (props) => {
    return(
        <Wrapper>
            <TitleText>{props.blog.title}</TitleText>
        </Wrapper>
    )
}

export default BlogItem;

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

  &:hover {
    background: lightgrey;
  }
`;

const TitleText = styled.p`
  margin: 0;
  font-size: 20px;
  font-weight: bold;
`;