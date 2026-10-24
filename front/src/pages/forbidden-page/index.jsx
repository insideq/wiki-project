import { Button } from "react-bootstrap";
import { ShieldX } from "react-bootstrap-icons";
import { Link } from "react-router-dom";

export const ForbiddenPage = () => {
    return (
        <div className="container text-center mt-5">
            <ShieldX size={64} className="text-danger mb-3" />
            <h1>403 — Доступ запрещён</h1>
            <p className="text-muted">У вас нет прав для просмотра этой страницы.</p>
            <Button as={Link} to="/wiki" variant="primary" className="mt-3">
                На главную
            </Button>
        </div>
    );
};