import api from '../../api/axios';
import Comment from '../../components/sample/Comment';

export const CommentPage = () => {
    console.log(`comment page load event`);
    // let comments = [];

    let Comments =[
       
        {
            "writer":"임창섭",
            "comment":"강사님과 함께하는 즐거운 react 수업입니다."
        },
        {
            "writer":"서정운",
            "comment":"강사님과 함께하는 재미없는 react 수업입니다."
        },    
        {
            "writer":"박선아",
            "comment":"강사님과 함께하는 즐겁지아니한 react 수업입니다."
        } 

    ];


    // const loadData = async () => {
    //     //데이터처리
    //     await api.get('/comment')
    //         .then(response => {
    //             console.log('response data:', response.data);
    //             comments = response.data;
    //         })
    //         .catch(err=>{
    //             console.error(`debug: error ${err}`);
    //         });
    // }

    // loadData();


    //UI
    return (
        <div>
            {
                Comments.map((comment, idx) => {
                    return <Comment 
                    key={idx}
                    data={comment}/>;

                })
            
            }

        </div>

    );

}

// export default CommentPage;