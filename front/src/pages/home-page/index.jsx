import { useTags } from "@entities/tag/hooks/use-tags";
import { useWikiPages } from "@entities/wiki-page/hooks/use-wiki-pages";
import { Badge, Button, Card, Col, Container, Row, Spinner } from "react-bootstrap";
import { BookHalf, PencilSquare } from "react-bootstrap-icons";
import { Link } from "react-router-dom";

export const HomePage = () => {
    const { data: recentPages, loading: loadingPages } = useWikiPages({ page: 1, size: 4 });
    const { data: tags } = useTags();

    return (
        <div>
            <div className="bg-primary text-white py-5 mb-5 shadow-sm" style={{ backgroundImage: 'linear-gradient(135deg, #0d6efd, #0dcaf0)' }}>
                <Container className="text-center py-4">
                    <BookHalf size={64} className="mb-3" />
                    <h1 className="display-4 fw-bold">WikiSystem</h1>
                    <p className="lead mb-4">Свободная энциклопедия, которую может редактировать каждый.</p>
                    <div className="d-flex justify-content-center gap-3">
                        <Button as={Link} to="/wiki" variant="light" size="lg" className="fw-bold">
                            Все статьи
                        </Button>
                        <Button as={Link} to="/wiki/new" variant="outline-light" size="lg" className="d-flex align-items-center gap-2">
                            <PencilSquare /> Создать статью
                        </Button>
                    </div>
                </Container>
            </div>

            <Container className="pb-5">
                <Row className="g-4">
                    <Col lg={8}>
                        <h3 className="mb-4 border-bottom pb-2">Свежие статьи</h3>
                        {loadingPages ? (
                            <Spinner animation="border" />
                        ) : (
                            <Row className="g-3">
                                {recentPages?.items?.map(wiki => (
                                    <Col md={6} key={wiki.id}>
                                        <Card className="h-100 shadow-sm border-0 hover-shadow transition">
                                            <Card.Body>
                                                <Card.Title className="fw-bold">
                                                    <Link to={`/wiki/${wiki.id}`} className="text-decoration-none text-dark">
                                                        {wiki.title}
                                                    </Link>
                                                </Card.Title>
                                                <Card.Text className="text-muted small">
                                                    {wiki.content || "Описание отсутствует..."}
                                                </Card.Text>
                                            </Card.Body>
                                            <Card.Footer className="bg-white border-0 text-muted small">
                                                Автор: {wiki.authorLogin}
                                            </Card.Footer>
                                        </Card>
                                    </Col>
                                ))}
                            </Row>
                        )}
                    </Col>

                    <Col lg={4}>
                        <h3 className="mb-4 border-bottom pb-2">Категории</h3>
                        <Card className="shadow-sm border-0">
                            <Card.Body>
                                {tags.length === 0 ? (
                                    <p className="text-muted">Тегов пока нет.</p>
                                ) : (
                                    <div className="d-flex flex-wrap gap-2">
                                        {tags.map(t => (
                                            <Badge 
                                                key={t.id} 
                                                as={Link} 
                                                to={`/wiki?tagId=${t.id}`} 
                                                bg="primary" 
                                                className="text-decoration-none px-3 py-2"
                                            >
                                                {t.name}
                                            </Badge>
                                        ))}
                                    </div>
                                )}
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};