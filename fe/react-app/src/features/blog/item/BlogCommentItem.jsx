import styled from "styled-components";
import Button from "../../../components/styled/Button";
import { useEffect, useState } from "react";
import TextInput from "../../../components/styled/TextInput";

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


const BlogCommentItem = ({comment, handler, updateHandler}) => {

    const [isEdit, setIsEdit]   = useState(false); 
    const [mention, setMention] = useState(''); 

    useEffect(() => {
        setMention(comment.comment);
    }, []);
    // 로그인사용자 이메일
    const user = localStorage.getItem('user');

    const updateMentionHandler = (e) => {
        if(!isEdit) {
            // 수정모드 ON
            setIsEdit(true);
        } else {
            /*
            SQL
            -   update  table 
                set     column = value 
                where   id = ? ; 
            - 선택된 특정댓글의 기본키 값을 가지고 사용자가 입력한 값으로 수정
            - UI - 일부 리렌더링(댓글 목록만)
            - 당연히 json-server 수정이 되어야 함.
            */
            // 수정완료모드 ON
            updateHandler(comment.id, mention); // read page : commnetUpdateHandler();
            setIsEdit(false) ;
        }
    }

    return(
        <Wrapper>
            {/* <CommentText>{comment.comment}</CommentText> */}
            <TextInput  height={16}
                        value={mention}
                        handler={ (e) => setMention(e.target.value) }
                        disabled={!isEdit} />
                        
            {
                user === comment.email &&
                    <div>
                        <Button title='삭제'
                                onClick={(e) => handler(e, comment.id)} /> 
                        &nbsp;&nbsp;
                        <Button title={ isEdit ? '수정완료' : '수정' }
                                onClick={(e) => updateMentionHandler(e)} />                     
                    </div>
            }
        </Wrapper>
    );
}

export default BlogCommentItem ;