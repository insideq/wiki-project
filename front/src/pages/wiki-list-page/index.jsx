import { useTags } from "@entities/tag/hooks/use-tags";
import { useWikiPages } from "@entities/wiki-page/hooks/use-wiki-pages";
import { useState } from "react";
import { Badge, Button, Card, Col, Form, ListGroup, Pagination, Placeholder, Row } from "react-bootstrap";
import { PlusCircle, Search, Tag } from "react-bootstrap-icons";
import { Link } from "react-router-dom";
import "./styles.css";

export const WikiListPage = () => {
    const [query, setQuery] = useState("");
    const [tagId, setTagId] = useState("");
    const [page, setPage] = useState(1);
    const [tagSearch, setTagSearch] = useState("");
    const { data, loading, error } = useWikiPages({ page, size: 10, query, tagId });
    const { data: tags } = useTags();

    const filteredTags = tags.filter(t => 
        t.name.toLowerCase().includes(tagSearch.toLowerCase())
    );

    return (
        <div className="container py-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="fw-bold m-0">Все статьи</h2>
                <Button as={Link} to="/wiki/new" variant="primary" className="d-flex align-items-center gap-2">
                    <PlusCircle /> Создать статью
                </Button>
            </div>

            <Row>
                <Col md={3} className="mb-4">
                    <Card className="shadow-sm border-0">
                        <Card.Header className="bg-white fw-bold d-flex align-items-center gap-2">
                            <Tag /> Категории
                        </Card.Header>
                        <Card.Body className="p-2 border-bottom">
                            <Form.Control 
                                size="sm" 
                                type="text" 
                                placeholder="Поиск тега..." 
                                value={tagSearch}
                                onChange={(e) => setTagSearch(e.target.value)}
                            />
                        </Card.Body>
                        <ListGroup variant="flush" style={{ maxHeight: '400px', overflowY: 'auto' }}>
                            <ListGroup.Item 
                                action 
                                active={tagId === ""} 
                                onClick={() => { setTagId(""); setPage(1); }}
                            >
                                Все статьи
                            </ListGroup.Item>
                            {filteredTags.map((t) => (
                                <ListGroup.Item 
                                    key={t.id} 
                                    action 
                                    active={tagId === String(t.id)}
                                    onClick={() => { setTagId(String(t.id)); setPage(1); }}
                                >
                                    {t.name}
                                </ListGroup.Item>
                            ))}
                            {filteredTags.length === 0 && (
                                <ListGroup.Item disabled>Теги не найдены</ListGroup.Item>
                            )}
                        </ListGroup>
                    </Card>
                </Col>

                <Col md={9}>
                    <Card className="mb-4 shadow-sm border-0 bg-light">
                        <Card.Body className="position-relative">
                            <Search className="position-absolute text-muted" style={{ left: 27, top: 27 }} />
                            <Form.Control
                                type="text"
                                placeholder="Поиск по заголовку и содержимому..."
                                value={query}
                                style={{ paddingLeft: 35 }}
                                onChange={(e) => {
                                    setQuery(e.target.value);
                                    setPage(1);
                                }}
                            />
                        </Card.Body>
                    </Card>

                    {error && <div className="alert alert-danger">{error}</div>}

                    {loading && (
                        <div className="d-flex flex-column gap-3">
                            {[1, 2, 3].map(i => (
                                <Card key={i} className="shadow-sm border-0">
                                    <Card.Body>
                                        <Placeholder as={Card.Title} animation="glow">
                                            <Placeholder xs={6} />
                                        </Placeholder>
                                        <Placeholder as={Card.Text} animation="glow">
                                            <Placeholder xs={8} /> <Placeholder xs={7} />
                                        </Placeholder>
                                    </Card.Body>
                                </Card>
                            ))}
                        </div>
                    )}

                    {!loading && !error && data?.items?.length === 0 && (
                        <div className="text-center text-muted py-5">
                            <h4>Ничего не найдено</h4>
                            <p>Попробуйте изменить категорию или параметры поиска.</p>
                        </div>
                    )}

                    {!loading && !error && data?.items?.length > 0 && (
                        <>
                            <div className="d-flex flex-column gap-3">
                                {data.items.map((wiki) => (
                                    <Link 
                                        to={`/wiki/${wiki.id}?search=${encodeURIComponent(query)}`} 
                                        className="text-decoration-none text-dark"
                                        key={wiki.id}
                                    >
                                        <Card className="shadow-sm border-0 hover-shadow transition h-100">
                                            <Card.Body>
                                                <div className="d-flex justify-content-between align-items-start mb-1">
                                                    <h4 className="mb-0 text-primary">
                                                        {wiki.title}
                                                    </h4>
                                                    {wiki.status === "PENDING_DELETION" && (
                                                        <Badge bg="danger">На удаление</Badge>
                                                    )}
                                                </div>
                                                
                                                <div className="text-muted small mb-2">
                                                    Автор: {wiki.authorLogin} · {new Date(wiki.createdAt).toLocaleDateString()}
                                                </div>
                                                
                                                {wiki.content && (
                                                    <p className="text-secondary mb-3" style={{ fontSize: '0.95rem' }}>
                                                        {query ? (
                                                            wiki.content.split(new RegExp(`(${query})`, "gi")).map((part, i) => 
                                                                part.toLowerCase() === query.toLowerCase() 
                                                                    ? <mark key={i} className="p-0 bg-warning">{part}</mark> 
                                                                    : part
                                                            )
                                                        ) : (
                                                            wiki.content
                                                        )}
                                                    </p>
                                                )}

                                                <div>
                                                    {wiki.tags?.map((tag) => (
                                                        <Badge key={tag.id} bg="light" text="dark" className="border me-1 fw-normal">
                                                            {tag.name}
                                                        </Badge>
                                                    ))}
                                                </div>
                                            </Card.Body>
                                        </Card>
                                    </Link>
                                ))}
                            </div>

                            {data.totalPages > 1 && (
                                <div className="mt-4 d-flex justify-content-center">
                                    <Pagination>
                                        <Pagination.Prev disabled={!data.hasPrevious} onClick={() => setPage(p => p - 1)} />
                                        {[...Array(data.totalPages)].map((_, idx) => {
                                            const p = idx + 1;
                                            if (p === 1 || p === data.totalPages || (p >= data.currentPage - 1 && p <= data.currentPage + 1)) {
                                                return <Pagination.Item key={p} active={p === data.currentPage} onClick={() => setPage(p)}>{p}</Pagination.Item>;
                                            }
                                            if (p === data.currentPage - 2 || p === data.currentPage + 2) {
                                                return <Pagination.Ellipsis key={p} disabled />;
                                            }
                                            return null;
                                        })}
                                        <Pagination.Next disabled={!data.hasNext} onClick={() => setPage(p => p + 1)} />
                                    </Pagination>
                                </div>
                            )}
                        </>
                    )}
                </Col>
            </Row>
        </div>
    );
};