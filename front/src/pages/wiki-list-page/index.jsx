import { useTags } from "@entities/tag/hooks/use-tags";
import { useWikiPages } from "@entities/wiki-page/hooks/use-wiki-pages";
import { useState } from "react";
import { Button, Card, Form, Spinner } from "react-bootstrap";
import { Link } from "react-router-dom";

export const WikiListPage = () => {
    const [query, setQuery] = useState("");
    const [tagId, setTagId] = useState("");
    const [page, setPage] = useState(1);
    const { data, loading, error } = useWikiPages({ page, size: 10, query, tagId });
    const { data: tags } = useTags();

    return (
        <div className="container mt-3">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Статьи</h2>
                <Button as={Link} to="/wiki/new" variant="primary">
                    Создать
                </Button>
            </div>

            <Form className="mb-3">
                <div className="row g-2">
                    <div className="col-md-6">
                        <Form.Control
                            type="text"
                            placeholder="Поиск..."
                            value={query}
                            onChange={(e) => {
                                setQuery(e.target.value);
                                setPage(1);
                            }}
                        />
                    </div>
                    <div className="col-md-4">
                        <Form.Select value={tagId} onChange={(e) => setTagId(e.target.value)}>
                            <option value="">Все теги</option>
                            {tags.map((t) => (
                                <option key={t.id} value={t.id}>
                                    {t.name}
                                </option>
                            ))}
                        </Form.Select>
                    </div>
                </div>
            </Form>

            {loading && <Spinner animation="border" />}
            {error && <div className="text-danger">{error}</div>}

            {data?.items?.length === 0 && <p>Статей нет.</p>}

            <div className="row g-3">
                {data?.items?.map((wiki) => (
                    <div key={wiki.id} className="col-md-6 col-lg-4">
                        <Card>
                            <Card.Body>
                                <Card.Title>{wiki.title}</Card.Title>
                                <Card.Subtitle className="mb-2 text-muted">
                                    Автор: {wiki.authorLogin}
                                </Card.Subtitle>
                                <div className="mb-2">
                                    {wiki.tags?.map((tag) => (
                                        <span key={tag.id} className="badge bg-secondary me-1">
                                            {tag.name}
                                        </span>
                                    ))}
                                </div>
                                <Link to={`/wiki/${wiki.id}`} className="btn btn-sm btn-outline-primary">
                                    Читать
                                </Link>
                            </Card.Body>
                        </Card>
                    </div>
                ))}
            </div>

            {data && data.totalPages > 1 && (
                <div className="mt-3 d-flex justify-content-center">
                    <Button disabled={!data.hasPrevious} onClick={() => setPage((p) => p - 1)}>
                        Назад
                    </Button>
                    <span className="mx-2 align-self-center">
                        {data.currentPage} / {data.totalPages}
                    </span>
                    <Button disabled={!data.hasNext} onClick={() => setPage((p) => p + 1)}>
                        Вперёд
                    </Button>
                </div>
            )}
        </div>
    );
};