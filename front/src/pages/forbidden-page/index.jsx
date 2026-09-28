import { Button, Container } from "react-bootstrap";
import { Link } from "react-router-dom";

export const ForbiddenPage = () => {
    return (
        <Container className="text-center">
            <h5>Доступ запрещен</h5>
            <p>Доступ к странице запрещен.</p>
            <Link to="/">
                <Button>На главную</Button>
            </Link>
        </Container>
    );
};
