import { useTags } from "@entities/tag/hooks/use-tags";
import { useWikiPages } from "@entities/wiki-page/hooks/use-wiki-pages";
import { useState } from "react";
import { Badge, Button, Card, Col, Form, ListGroup, Pagination, Row, Spinner } from "react-bootstrap";
import { PlusCircle, Search, Tag } from "react-bootstrap-icons";
import { Link } from "react-router-dom";
import "./styles.css";

export const WikiListPage = () => {
    const [query, setQuery] = useState("");
    const [selectedTagIds, setSelectedTagIds] = useState([]);
    const [page, setPage] = useState(1);
    const [tagSearch, setTagSearch] = useState("");
    
    const { data, loading, error } = useWikiPages({ page, size: 10, query, tagIds: selectedTagIds });
    const { data: tags } = useTags();

    const filteredTags = tags.filter(t => t.name.toLowerCase().includes(tagSearch.toLowerCase()));

    // Обработчик выбора тега (добавляет/удаляет из массива)
    const handleTagToggle = (id) => {
        setSelectedTagIds(prev => 
            prev.includes(id) ? prev.filter(t => t !== id) : [...prev, id]
        );
        setPage(1);
    };

    return (
        <div className="container py-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="fw-bold m-0">Все статьи</h2>
                <Button as={Link} to="/wiki/new" variant="primary" className="d-flex align-items-center gap-2">
                    <PlusCircle /> Создать статью
                </Button>
            </div>

            <Row>
                {/* ЛЕВЫЙ САЙДБАР */}
                <Col md={3} className="mb-4">
                    <Card className="shadow-sm border-0">
                        <Card.Header className="bg-white fw-bold d-flex align-items-center gap-2">
                            <Tag /> Категории
                        </Card.Header>
                        <Card.Body className="p-2 border-bottom">
                            <Form.Control size="sm" type="text" placeholder="Поиск тега..." value={tagSearch} onChange={(e) => setTagSearch(e.target.value)} />
                        </Card.Body>
                        <ListGroup variant="flush" className="tag-sidebar-list">
                            <ListGroup.Item 
                                action 
                                active={selectedTagIds.length === 0} 
                                onClick={() => { setSelectedTagIds([]); setPage(1); }}
                            >
                                Все статьи
                            </ListGroup.Item>
                            {filteredTags.map((t) => (
                                <ListGroup.Item key={t.id} className="py-1">
                                    <Form.Check 
                                        type="checkbox"
                                        id={`tag-${t.id}`}
                                        label={t.name}
                                        checked={selectedTagIds.includes(t.id)}
                                        onChange={() => handleTagToggle(t.id)}
                                        className="m-0"
                                    />
                                </ListGroup.Item>
                            ))}
                        </ListGroup>
                    </Card>
                </Col>

                {/* ПРАВАЯ ЧАСТЬ */}
                <Col md={9}>
                    <Card className="mb-4 shadow-sm border-0 bg-light">
                        <Card.Body className="position-relative">
                            <Search className="position-absolute text-muted" style={{ left: 20, top: 15 }} />
                            <Form.Control
                                type="text"
                                placeholder="Поиск по заголовку и содержимому..."
                                value={query}
                                style={{ paddingLeft: 35 }}
                                onChange={(e) => { setQuery(e.target.value); setPage(1); }}
                            />
                        </Card.Body>
                    </Card>

                    {error && <div className="alert alert-danger">{error}</div>}
                    {loading && <Spinner animation="border" />}
                    
                    {!loading && !error && data?.items?.length === 0 && (
                        <div className="text-center text-muted py-5">
                            <h4>Ничего не найдено</h4>
                            <p>Попробуйте изменить категории или запрос.</p>
                        </div>
                    )}

                    {!loading && !error && data?.items?.length > 0 && (
                        <>
                            <div className="d-flex flex-column gap-3">
                                {data.items.map((wiki) => (
                                    <Link key={wiki.id} to={`/wiki/${wiki.id}?search=${encodeURIComponent(query)}`} className="text-decoration-none">
                                        <Card className="wiki-list-card h-100">
                                            <Card.Body>
                                                <div className="d-flex justify-content-between align-items-start mb-1">
                                                    <h4 className="wiki-card-title mb-0">{wiki.title}</h4>
                                                    {wiki.status === "PENDING_DELETION" && <Badge bg="danger">На удаление</Badge>}
                                                </div>
                                                <div className="text-muted small mb-2">
                                                    Автор: <span className="fw-bold">{wiki.authorLogin}</span> · {new Date(wiki.createdAt).toLocaleDateString()}
                                                </div>
                                                {wiki.content && (
                                                    <p className="text-secondary mb-3 wiki-card-text">
                                                        {query ? (
                                                            wiki.content.split(new RegExp(`(${query})`, "gi")).map((part, i) => 
                                                                part.toLowerCase() === query.toLowerCase() 
                                                                    ? <mark key={i} className="p-0 bg-warning">{part}</mark> 
                                                                    : part
                                                            )
                                                        ) : (wiki.content)}
                                                    </p>
                                                )}
                                                <div>
                                                    {wiki.tags?.map((tag) => (
                                                        <Badge key={tag.id} bg="light" text="dark" className="border me-1 fw-normal">{tag.name}</Badge>
                                                    ))}
                                                </div>
                                            </Card.Body>
                                        </Card>
                                    </Link>
                                ))}
                            </div>

                            {data.totalPages > 1 && (
                                <div className="mt-4 d-flex justify-content-center">
                                    <Pagination> ... </Pagination>
                                </div>
                            )}
                        </>
                    )}
                </Col>
            </Row>
        </div>
    );
};