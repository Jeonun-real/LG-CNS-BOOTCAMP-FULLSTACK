

//npm install @mui/material @emotion/react @emotion/styled

import Button from '@mui/material/Button';
const MaterialButton = (props) => {
    
    return (
        <Button onClick={props.onClick}>
            {props.title}
        </Button>
    );

}

export default MaterialButton;