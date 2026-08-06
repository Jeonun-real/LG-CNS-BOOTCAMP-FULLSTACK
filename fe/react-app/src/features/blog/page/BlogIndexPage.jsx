import styled from "styled-components";
import Button from "../../../components/styled/Button" ;
import BlogList from "../list/BlogList";
import { useEffect, useState } from "react";
import api from "../../../api/axios";
import '../ui/blog.css';
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

const LogoutButton = styled(Button)`
    background-color: #f44336;
    color: white;

    &:hover {
        background-color: #d32f2f;
    }
`;

// ---------- 버튼 영역 ----------
const ButtonRow = styled.div`
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;

    & > button {
        box-sizing: border-box;
        height: 40px;
        outline: none;
    }

    & > button:focus {
        outline: none;
        box-shadow: none;
    }
`;

// ---------- 카테고리 필터 UI ----------
const CategoryRow = styled.div`
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    width: 100%;
    margin-top: 24px;
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

    border: 1.5px solid ${(props) => (props.$active ? "#6366f1" : "#e5e7eb")};
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
// blog property - title, content, category, email(pk)
const BlogIndexPage = () => {
    
    const CATEGORIES = ["전체", "개발", "생활", "취미", "일상"];
    
    const user = localStorage.getItem('user');

    const [blogs , setBlogs] = useState([]);
    // const blogs = [
    //     {
    //         "title"    : "til 작성",
    //         "content"  : "component",
    //         "category" : "front-end",
    //         "email"    : user
    //     },
    //     {
    //         "title"    : "링거(술)",
    //         "content"  : "java",
    //         "category" : "back-end",
    //         "email"    : user
    //     }
    // ];

    /*
    Q)
    - axios 통신(get(blogs) , params X)
    - 데이터를 reactive state 관리(setXXXX) 
    - 렌더링시점에 데이터 바인딩이 X, side effect  필요함!!
    */
    const loadData = async () => {
        // json-server version
        await api.get(`/blogs`)
                .then( response => {
                    console.log(`debug >>>> axios request success` , response);  
                    if(response.status === 200) {
                        setBlogs(response.data);
                    }
                })
                .catch( error => {
                    console.log(`debug >>>> axios request error` , error); 
                });
    }
    useEffect(() => {
        loadData() ;
    }, []);
    
    
    // 선택된 카테고리에 따라 blogs 필터링
    const [selectedCategory, setSelectedCategory] = useState("전체");
    const filteredBlogs = selectedCategory === "전체"
        ? blogs
        : blogs.filter((blog) => blog.category === selectedCategory);


    const moveUrl = useNavigate();
    // handler
    const writeHandler = (e) => {
        moveUrl('/blogs/write'); 
    };

    return (
        <Wrapper>
            <Container>
                {user && <WelcomeMessage>{user}님 환영합니다.</WelcomeMessage>}
                <ButtonRow>
                    <Button title='글 작성하기'
                            onClick={(e) => writeHandler(e)}></Button>
                    <Button title='로그아웃'></Button>
                    <Button title='기상예보'></Button>
                </ButtonRow>

                <CategoryRow>
                    {CATEGORIES.map((category) => (
                        <CategoryChip
                            key={category}
                            $active={category === selectedCategory}
                            onClick={() => setSelectedCategory(category)}
                        >
                            {category}
                        </CategoryChip>
                    ))}
                </CategoryRow>

                <BlogList ary={blogs || []}/>

            </Container>
        </Wrapper>
    );
}
export default BlogIndexPage ;

