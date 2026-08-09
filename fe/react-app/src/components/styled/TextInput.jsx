import styled from "styled-components";

const StyledTextArea = styled.textarea`
    width: calc(100% - 32px);
    ${(props) =>
        props.height &&
        `
        height: ${props.height}px;
    `}
    padding: 16px;
    font-size: 16px;
    line-height: 20px;
    margin-down : 16px ;
    margin-top  : 16px ;
`;

const TextInput = ({height, value, handler, disabled}) => {
    return(
        <StyledTextArea 
                    height={height}
                    value={value}
                    onChange={handler}
                    disabled={disabled} />
    );
}

export default TextInput ; 
