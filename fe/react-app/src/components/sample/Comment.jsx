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
                <span>책이름 : {data.writer}</span><p/>
                <span>책가격 : {data.comment}</span>

            </div>
        </div>
    );
}

    export default Comment;