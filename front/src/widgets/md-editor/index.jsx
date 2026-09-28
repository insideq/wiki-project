import MDEditor from "@uiw/react-md-editor";

export const MdEditor = ({ value, onChange }) => {
    return (
        <div data-color-mode="light">
            <MDEditor value={value} onChange={onChange} height={400} />
        </div>
    );
};