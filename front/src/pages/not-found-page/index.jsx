import { Button } from "react-bootstrap";
import { Search } from "react-bootstrap-icons";
import { Link } from "react-router-dom";

export const NotFoundPage = () => {
    return (
        <div className="container text-center mt-5">
            <Search size={64} className="text-secondary mb-3" />
            <h1>404 — Страница не найдена</h1>
            <p className="text-muted">Возможно, она была удалена или вы ошиблись адресом.</p>
            <Button as={Link} to="/wiki" variant="primary" className="mt-3">
                Вернуться к статьям
            </Button>
        </div>
    );
};