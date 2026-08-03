const container = document.querySelector('#card-container');



const showSkeletionUI = () =>{
    const skeletons = Array.from({length:6},rederSkeletonCard).join('');
    console.log('skeleton',skeletons);
    container.innerHTML = skeletons;
}

const rederSkeletonCard = ()=>{
    return `
        <div class="skeleton-card">
            <div class="skeleton-img"></div>

            <div class="skeleton-body">
                <div class="skeleton-line short"></div>
                <div class="skeleton-line title"></div>
                <div class="skeleton-line text"></div>
                <div class="skeleton-line text"></div>
            </div>
        </div>
    `;
}




const rederCard = (item)=>{
    return `
        <div class="card">
            <img
                class="card-img"
                src="${item.image}"
                alt="${item.title}"
            >

            <div class="card-body">
                ${
                    item.tag
                        ? `<span class="card-tag">${item.tag}</span>`
                        : ''
                }

                <h3 class="card-title">${item.title}</h3>
                <p class="card-desc">${item.description}</p>
                <div class="card-price">${item.price.toLocaleString()}원</div>
            </div>
        </div>
    `;

}

const rederCards = (items)=>{

    console.log('container',container);
    container.innerHTML = items.map(rederCard).join("");

}

const delay = (ms)=>{
    return new Promise((resolve) => setTimeout(resolve,ms));
}


const loadData = async() => {
    console.log(`데이터  로드시 스텔레톤 UI 표출`);
    showSkeletionUI();

    const [response] = await Promise.all([        
        axios.get('../server/data.json'),
        delay(1500)
    ]);

    console.log('data',response.data);
    rederCards(response.data);
}

loadData();