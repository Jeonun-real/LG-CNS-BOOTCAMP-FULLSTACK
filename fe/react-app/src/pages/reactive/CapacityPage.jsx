import Button from '../../components/styled/Button';
import { useState, useEffect } from 'react';

const CapacityPage = () => {
    console.log('debug: CapacityPage lifecycle update');
    
    //고민?
    //해단변수는 양방향 실시간 소통이 가능해야 함.
    // let cnt = 0;
    // solution = hook state : useState()
    const capacity =10;

    let[cnt,setCnt] = useState(0); //int 초기화
    let[full,setFull]=useState(false); //boolean 초기화
    let[empty,setEmpty]=useState(false); //boolean 초기화


    const upCntHandler = (e) => {
        // cnt = cnt+1;
        setCnt(cnt => cnt+1);
        // console.log(`upHandler: ${cnt}`);
    }
    const downCntHandler = (e) => {
        setCnt(cnt => cnt-1);
        // console.log(`downHandler: ${cnt}`);
    }

    // side effect으로 렌더링 이후 작업이 필요한 경우?
    // useEffect() 함수를 이용하여 작업할 수 있다.
    // useEffect() 함수를 이용해서 생명주기 관리 가능
    useEffect(()=>{
        console.log('capacitypage lifecycle update');
        console.log(`debug>>side effect cnt: ${cnt}`);
        setFull(cnt>=capacity);
        setEmpty(cnt<=0);

    },[cnt]);

    //////////////////////////////////////////////////
    /* 
    - 입장인원(10명)
    - 입장, 퇴장 버튼을 만들어서
    - 입장버튼을 클릭하면 인원수가 증가되고 입장인원이 꽉차면 버튼 비활성화
    - 퇴장버튼을 클릭하면 인원수가 감소되고 입장인원이 0명이면 버튼 비활성화
    */
    return(
        <div>
            <p>입장인원: {cnt}</p>
            <Button 
                title="입장" 
                onClick={(e)=>upCntHandler()}
                disabled={full} 
            />
            <Button 
                title="퇴장" 
                onClick={(e)=>downCntHandler()} 
                disabled={empty} 
            />
        </div>
    );
}

export default CapacityPage;