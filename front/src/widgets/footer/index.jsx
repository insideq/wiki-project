import "./styles.css";

export const Footer = () => {
    return (
        <footer className="d-flex flex-shrink-0 align-items-center justify-content-center">
            Wiki-system, {new Date().getFullYear()}
        </footer>
    );
};