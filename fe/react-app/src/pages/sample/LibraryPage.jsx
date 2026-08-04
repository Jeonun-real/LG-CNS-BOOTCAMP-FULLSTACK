import Book from "../../components/sample/Book";

const LibraryPage = () => {
    // jsx = script + html
 
    
    //script
    const books = [
        { category: 'IT', bookName: 'java', price: 30000 },
        { category: 'IT', bookName: 'python', price: 25000 },
        { category: 'lang', bookName: 'javascript', price: 20000 },
        { category: 'lang', bookName: 'react', price: 35000 },
        { category: 'essay', bookName: 'spring', price: 40000 },
        { category: 'essay', bookName: 'nodejs', price: 30000 }
    ];

    //ui template
    // html 에서 스크입트 변수를 사용하려면 {} 중괄호를 사용해야 한다.
    return (
        <div>
            {
                books.filter(book => book.category==='lang')
                .map((book, idx) => {
                    return <Book 
                                key={idx} 
                                book ={book}
                             />
                })

            }
        </div>
    );

}


export default LibraryPage;