import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

export const MdViewer = ({ content }) => {
    return (
        <div className="md-viewer">
            <ReactMarkdown remarkPlugins={[remarkGfm]}>{content || ""}</ReactMarkdown>
        </div>
    );
};