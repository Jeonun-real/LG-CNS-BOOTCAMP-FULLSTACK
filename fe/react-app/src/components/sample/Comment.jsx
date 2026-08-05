import '../../styles/book.css';
import placeholder from '../../img/placeholder.png';

const Comment = ({ data }) => {
    return (
        <div className='wrapper'>
            <div>
                <img src={placeholder} 
                    className='image'>
                </img>
            </div>
            <div>
                <span>작성자 : {data.writer}</span><p/>
                <span>댓글 : {data.comment}</span>

            </div>
        </div>
    );
}

    export default Comment;