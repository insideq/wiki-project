import { useWikiPages } from "@entities/wiki-page/hooks/use-wiki-pages";
import { Button, Card, Col, Container, Row, Spinner } from "react-bootstrap";
import { Link } from "react-router-dom";

export const HomePage = () => {
    const { data, loading } = useWikiPages({ page: 1, size: 3 });

    return (
        <Container className="py-5">
            <div className="text-center mb-5">
                <h1 className="display-4 fw-bold">Добро пожаловать в WikiSystem</h1>
                <p className="lead text-muted">Свободная энциклопедия, которую может редактировать каждый.</p>
                <Button as={Link} to="/wiki" variant="primary" size="lg" className="mt-2">
                    Перейти ко всем статьям
                </Button>
            </div>

            <h3 className="mb-4 border-bottom pb-2">Случайные статьи</h3>
            {loading ? (
                <Spinner animation="border" />
            ) : (
                <Row className="g-4">
                    {data?.items?.map(wiki => (
                        <Col md={4} key={wiki.id}>
                            <Card className="h-100 shadow-sm border-0">
                                <Card.Body>
                                    <Card.Title className="fw-bold">{wiki.title}</Card.Title>
                                    <Card.Text className="text-muted small">
                                        {wiki.content || "Описание отсутствует..."}
                                    </Card.Text>
                                    <Button as={Link} to={`/wiki/${wiki.id}`} variant="outline-primary" size="sm">
                                        Читать
                                    </Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}
        </Container>
    );
};