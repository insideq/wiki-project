import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import "./styles.css";

export const MdViewer = ({ content, highlight }) => {
    // Функция подсветки текста (оборачивает найденные слова в <mark>)
    const highlightText = (text) => {
        if (!highlight || !text || typeof text !== "string") return text;
        const regex = new RegExp(`(${highlight})`, "gi");
        return text.split(regex).map((part, i) =>
            part.toLowerCase() === highlight.toLowerCase() 
                ? <mark key={i} className="bg-warning p-0">{part}</mark> 
                : part
        );
    };

    return (
        <div className="md-viewer">
            <ReactMarkdown 
                remarkPlugins={[remarkGfm]}
                components={{
                    // Правильно переопределяем только текстовые узлы
                    text: ({ children }) => <>{highlightText(children)}</>
                }}
            >
                {content || ""}
            </ReactMarkdown>
        </div>
    );
};