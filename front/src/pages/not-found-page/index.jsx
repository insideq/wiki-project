import { Button, Container } from "react-bootstrap";
import { Link } from "react-router-dom";

export const NotFoundPage = () => {
    return (
        <Container className="text-center">
            <h5>Страница не найдена</h5>
            <p>Страница, которую Вы ищете, не существует.</p>
            <Link to="/">
                <Button>На главную</Button>
            </Link>
        </Container>
    );
};
