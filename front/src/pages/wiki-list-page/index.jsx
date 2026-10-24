import { useTags } from "@entities/tag/hooks/use-tags";
import { useWikiPages } from "@entities/wiki-page/hooks/use-wiki-pages";
import { useState } from "react";
import { Badge, Button, Card, Form, Pagination, Placeholder } from "react-bootstrap";
import { PlusCircle, Search } from "react-bootstrap-icons";
import { Link } from "react-router-dom";

export const WikiListPage = () => {
    const [query, setQuery] = useState("");
    const [tagId, setTagId] = useState("");
    const [page, setPage] = useState(1);
    const { data, loading, error } = useWikiPages({ page, size: 9, query, tagId });
    const { data: tags } = useTags();

    return (
        <div className="container py-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="fw-bold m-0">База знаний</h2>
                <Button as={Link} to="/wiki/new" variant="primary" className="d-flex align-items-center gap-2">
                    <PlusCircle /> Создать статью
                </Button>
            </div>

            <Card className="mb-4 shadow-sm border-0 bg-light">
                <Card.Body className="d-flex gap-3 flex-wrap">
                    <div className="flex-grow-1 position-relative">
                        <Search className="position-absolute text-muted" style={{ left: 12, top: 10 }} />
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
                    </div>
                    <Form.Select 
                        style={{ maxWidth: "250px" }} 
                        value={tagId} 
                        onChange={(e) => { setTagId(e.target.value); setPage(1); }}
                    >
                        <option value="">Все теги</option>
                        {tags.map((t) => (
                            <option key={t.id} value={t.id}>{t.name}</option>
                        ))}
                    </Form.Select>
                </Card.Body>
            </Card>

            {error && <div className="alert alert-danger">{error}</div>}

            {/* Скелетон при загрузке */}
            {loading && (
                <div className="row g-3">
                    {[1, 2, 3, 4, 5, 6].map(i => (
                        <div key={i} className="col-md-6 col-lg-4">
                            <Card className="h-100 shadow-sm border-0">
                                <Card.Body>
                                    <Placeholder as={Card.Title} animation="glow">
                                        <Placeholder xs={8} />
                                    </Placeholder>
                                    <Placeholder as={Card.Text} animation="glow">
                                        <Placeholder xs={4} /> <Placeholder xs={6} /> <Placeholder xs={7} />
                                    </Placeholder>
                                </Card.Body>
                            </Card>
                        </div>
                    ))}
                </div>
            )}

            {!loading && !error && data?.items?.length === 0 && (
                <div className="text-center text-muted py-5">
                    <h4>Ничего не найдено</h4>
                    <p>Попробуйте изменить параметры поиска.</p>
                </div>
            )}

            {!loading && !error && data?.items?.length > 0 && (
                <>
                    <div className="row g-3">
                        {data.items.map((wiki) => (
                            <div key={wiki.id} className="col-md-6 col-lg-4">
                                <Card className="h-100 shadow-sm border-0 hover-shadow transition">
                                    <Card.Body className="d-flex flex-column">
                                        <div className="d-flex justify-content-between align-items-start mb-2">
                                            <Card.Title className="fw-bold mb-0">{wiki.title}</Card.Title>
                                            {wiki.status === "PENDING_DELETION" && (
                                                <Badge bg="danger">На удаление</Badge>
                                            )}
                                        </div>
                                        <Card.Subtitle className="mb-3 text-muted small">
                                            Автор: {wiki.authorLogin} <br/>
                                            {new Date(wiki.createdAt).toLocaleDateString()}
                                        </Card.Subtitle>

                                        {wiki.content && (
                                            <Card.Text className="text-secondary small mb-3" style={{ fontSize: '0.85rem' }}>
                                                {query ? (
                                                    wiki.content.split(new RegExp(`(${query})`, "gi")).map((part, i) => 
                                                        part.toLowerCase() === query.toLowerCase() 
                                                            ? <mark key={i} className="p-0 bg-warning">{part}</mark> 
                                                            : part
                                                    )
                                                ) : (
                                                    wiki.content
                                                )}
                                            </Card.Text>
                                        )}
                                        
                                        <div className="mb-3 flex-grow-1">
                                            {wiki.tags?.map((tag) => (
                                                <Badge key={tag.id} bg="secondary" className="me-1 fw-normal">
                                                    {tag.name}
                                                </Badge>
                                            ))}
                                        </div>
                                        
                                        <Button as={Link} to={`/wiki/${wiki.id}?search=${encodeURIComponent(query)}`} variant="outline-primary" size="sm" className="w-100">
                                            Читать статью
                                        </Button>
                                    </Card.Body>
                                </Card>
                            </div>
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
        </div>
    );
};