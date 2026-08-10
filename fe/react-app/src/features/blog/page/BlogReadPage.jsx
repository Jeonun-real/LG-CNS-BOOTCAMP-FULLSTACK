import { useEffect, useState }      from "react";
import { useNavigate, useParams }   from "react-router-dom";
import styled, {keyframes}          from "styled-components";
import api                          from "../../../api/axios";
import Button                       from "../../../components/styled/Button";
import TextInput from "../../../components/styled/TextInput";
import BlogCommentList from "../list/BlogCommentList";


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

    & > * {
        :not(:last-child) {
            margin-bottom: 16px;
        }
    }
`;

const PostContainer = styled.div`
    padding: 8px 16px;
    border: 1px solid grey;
    border-radius: 8px;
`;

const TitleText = styled.p`
    font-size: 28px;
    font-weight: 500;
`;

const ContentText = styled.p`
    font-size: 20px;
    line-height: 32px;
    white-space: pre-wrap;
`;

const CommentLabel = styled.p`
    font-size: 16px;
    font-weight: 500;
`;

const spin = keyframes`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`;

const Spinner = styled.div`
  border: 6px solid #f3f3f3;
  border-top: 6px solid #3498db;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  animation: ${spin} 1s linear infinite;
  margin: 100px auto;
`;

const WelcomeMessage = styled.div`
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 16px;
    color: #333;
`;


const BlogReadPage = () => {
    const {blogId} = useParams();
    const user = localStorage.getItem('user');
    console.log(`debug >>>> BlogReadPage rendering ${blogId} , ${user}`); 


    // 블로그정보
    const [blog, setBlog]           = useState({});
    
    // 댓글배열정보
    const [comments, setComments]   = useState({});

    // 댓글입력
    const [comment, setComment]     = useState('');


    // navigate
    const moveUrl = useNavigate();

    /*
    Q)
    - component mount 일때 blogId 해당하는 객체를 얻어올 수 있어야함
    - api .get('/blogs/blogId') : blog
    - blog 는 reactive state 상태로 관리  : useState();
    */
    const loadData = async () => {
        // json-server : 댓글이 없는 상황 
        /*
        - axios - get(blogs?key=value&key=value)
        - axios - get(blogs , {
            params : {} 
        })
        - axios - get(blogs/&{}/&{}/&{})
        - sql : select * from table where id = ? ;
        */
        // const id = blogId
        // await api.get(`/blogs/${id}`)
        //         .then( response => {
        //             console.log(`debug >>>> axios request success` , response);  
        //             if(response.status === 200) {
        //                 setBlog(response.data); 
        //             }
        //         })
        //         .catch( error => {
        //             console.log(`debug >>>> axios request error` , error); 
        //         });    
        
        // json-server : 댓글이 있는 상황 
        // 1:N 관계
        // - axios - get(blogs/&{}?_embed=comments)
        const id = blogId
        await api.get(`/blogs/${id}?_embed=comments`)
                .then( response => {
                    console.log(`debug >>>> axios request success` , response);  
                    if(response.status === 200) {
                        setBlog(response.data); 
                        
                        // 댓글정보 배열에 담아본다면? - reactive state 
                        setComments(response.data.comments) ;
                    }
                })
                .catch( error => {
                    console.log(`debug >>>> axios request error` , error); 
                });    

    }
    useEffect(() => {
        loadData();
    }, []);

    // comment handler
    const commentHandler = async (e) => {
        /*
        Q)
        - data - (comment, blogId, email)
        - axio post(url, data)
        - status 201(Created) 
        - 부분 리렌더링을 위한 작업(comments - 배열) 
        */
        console.log(`debug >>>> commentHandler event`);
        let email = user ;
        await api.post('/comments', {blogId : Number(blogId), comment, email})
                .then( response => {

                    console.log(`debug >>>> axios request success` , response);  
                    
                    if(response.status === 201) {
                        setComments( ary => {
                            return [...ary, response.data]
                        }); 
                        setComment('');
                    }
                })
                .catch( error => {
                    console.log(`debug >>>> axios request error` , error); 
                });
    };

    // comment delete handler 
    const commentDeleteHandler = async (e, id) => {
        console.log(`debug >>>> commentDeleteHandler event`);
        console.log(`debug >>>> commentDeleteHandler comment id ${id}`);
        /*
        Q)
        - axios delete('/comments/${id}') , status 204(NO_CONTENT) 
        - 삭제될 comment id 만 필터링해서 re-rendering 
        */
        await api.delete(`/comments/${id}`)
                .then( response => {
                    console.log(`debug >>>> axios request success` , response);  
                    if(response.status === 200) {
                        setComments(comments.filter( (c) => {
                            return c.id !== id 
                        }));
                    }
                })
                .catch( error => {
                    console.log(`debug >>>> axios request error` , error); 
                });
    }

    // comment update
    const commentUpdateHandler = async (id, mention) => {
        console.log(`debug >>>> commentUpdateHandler event`);
        console.log(`debug >>>> commentUpdateHandler id ${id}, mention ${mention}`);

        // update : axios put(전체교체), patch(부분수정) 
        await api.patch(`/comments/${id}` , {
            comment : mention
        })
        .then( response => {
            console.log(`debug >>>> axios request success` , response);  
            if(response.status === 200) {
                setComments( ary => {
                    return ary.map( comment => {
                        return comment.id === id ? {...comment , comment : mention} : comment
                    })
                })        
            }
        })
        .catch( error => {
            console.log(`debug >>>> axios request error` , error); 
        });
    }




    return(
        <Wrapper>
                    
            { !blog.id && <Spinner /> }
            { blog.id && 
               <Container>
                    {user && <WelcomeMessage>{user}님 환영합니다.</WelcomeMessage>} 
                    <Button title='메인페이지' 
                            onClick={() => {
                                moveUrl('/blogs/index');
                            }}/>
                    
                    {/* blog title, content, category */}
                    <PostContainer>
                        <TitleText>{blog.title}</TitleText>
                        <ContentText>{blog.content}</ContentText>
                    </PostContainer>

                    {/* 댓글 UI 설계 - BlogCommentList , BlogCommentItem */}
                    <CommentLabel>작성된 댓글 목록</CommentLabel>
                    

                    {/*  BlogCommentList  */}
                    <BlogCommentList    comments={comments || [] } 
                                        handler={commentDeleteHandler}
                                        updateHandler={commentUpdateHandler} />        

                    
                    {/* 댓글 입력과 이벤트  */}
                    <TextInput  height={14} 
                                value={comment}
                                handler={ (e) => {
                                    setComment(e.target.value) ;
                                }}
                                />
                    <Button     title='댓글 작성'
                                onClick={commentHandler}/>        

               </Container> 
            }
            
        </Wrapper>
    );
}
export default BlogReadPage ;