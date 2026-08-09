import { useEffect, useState } from 'react';
import api          from '../../api/axios' ;
import Comment      from '../../components/sample/Comment';

export const CommentPage = () => {
    // script
    console.log(`debug >>>> CommentPage load event `);
    let [comments, setComments] = useState([]);
    const loadData = async () => {
        await api.get('/comment')
                .then( response => {
                    console.log(`debug >>>> response `, response.data ); 
                    setComments(response.data) ;
                })
                .catch( err => {
                    console.log(`debug >>>> err `, err);
                });
    }
    useEffect(() => {
        loadData() ;    
    }, [] ); 
    


    // UI
    return(
        <div>
            {
                comments?.map( (comment, idx) => {
                    return <Comment 
                                key={idx}
                                data={comment} />
                }) ?? [] 
            }
        </div>
    );

}

// export default CommentPage ;

