import { useTags } from "@entities/tag/hooks/use-tags";
import { useWikiMutations } from "@entities/wiki-page/hooks/use-wiki-mutations";
import { useWikiPage } from "@entities/wiki-page/hooks/use-wiki-page";
import {
    WIKI_PAGE_EDIT_POLICY,
    WIKI_PAGE_EDIT_POLICY_LABELS,
    WIKI_PAGE_VISIBILITY,
    WIKI_PAGE_VISIBILITY_LABELS,
} from "@entities/wiki-page/model/enums";
import { useAuth } from "@shared/auth";
import { MdEditor } from "@widgets/md-editor";
import { useEffect, useState } from "react";
import { Alert, Button, Form, Spinner } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import "./styles.css";

export const WikiEditPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;
    
    const { create, update } = useWikiMutations();
    const { data: existing, loading: loadingExisting, error: loadError } = useWikiPage(id);
    const { data: tags } = useTags();
    const { user, isAuthenticated } = useAuth();

    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [visibility, setVisibility] = useState(WIKI_PAGE_VISIBILITY.PUBLIC);
    const [editPolicy, setEditPolicy] = useState(WIKI_PAGE_EDIT_POLICY.OPEN);
    const [selectedTagIds, setSelectedTagIds] = useState([]);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!isAuthenticated) {
            navigate("/login");
        }
    }, [isAuthenticated, navigate]);

    useEffect(() => {
        if (existing) {
            setTitle(existing.title || "");
            setContent(existing.content || "");
            setVisibility(existing.visibility || WIKI_PAGE_VISIBILITY.PUBLIC);
            setEditPolicy(existing.editPolicy || WIKI_PAGE_EDIT_POLICY.OPEN);
            setSelectedTagIds(existing.tags?.map((t) => t.id) || []);
        }
    }, [existing]);

    useEffect(() => {
        if (isEdit && existing && user?.details) {
            const isAdmin = user.details.role === "ROLE_ADMIN";
            const isAuthor = user.details.login === existing.authorLogin;
            const isOpen = existing.editPolicy === WIKI_PAGE_EDIT_POLICY.OPEN;
            
            if (!isAdmin && !isAuthor && !isOpen) {
                navigate("/forbidden");
            }
        }
    }, [isEdit, existing, user, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setError(null);
        const payload = {
            title,
            content,
            visibility,
            editPolicy,
            tagIds: selectedTagIds,
        };
        try {
            const result = isEdit ? await update(id, payload) : await create(payload);
            navigate(`/wiki/${result.id}`);
        } catch (e) {
            setError(e.message);
        } finally {
            setSaving(false);
        }
    };

    if (isEdit && loadingExisting) return <div className="text-center mt-5"><Spinner animation="border" /></div>;
    if (isEdit && loadError) return <div className="container mt-3"><Alert variant="danger">{loadError}</Alert></div>;
    if (!user) return <div className="text-center mt-5"><Spinner animation="border" /></div>;

    return (
        <div className="container mt-4">
            <h2 className="mb-4 fw-bold">{isEdit ? "Редактирование статьи" : "Создание статьи"}</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            {/* 2. Обертка формы */}
            <div className="wiki-edit-form">
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-4">
                        <Form.Label className="fw-bold">Заголовок статьи</Form.Label>
                        <Form.Control 
                            type="text" 
                            value={title} 
                            onChange={(e) => setTitle(e.target.value)} 
                            required 
                            maxLength={200} 
                            placeholder="Введите заголовок..."
                            size="lg"
                        />
                    </Form.Group>

                    <Form.Group className="mb-4">
                        <Form.Label className="fw-bold">Содержимое (Markdown)</Form.Label>
                        <MdEditor value={content} onChange={setContent} />
                    </Form.Group>

                    <div className="row mb-4">
                        <Form.Group className="col-md-6">
                            <Form.Label className="fw-bold">Видимость</Form.Label>
                            <Form.Select value={visibility} onChange={(e) => setVisibility(e.target.value)}>
                                {Object.entries(WIKI_PAGE_VISIBILITY_LABELS).map(([key, label]) => (
                                    <option key={key} value={key}>{label}</option>
                                ))}
                            </Form.Select>
                            <Form.Text className="text-muted">
                                Приватные статьи видят только авторизованные пользователи.
                            </Form.Text>
                        </Form.Group>

                        <Form.Group className="col-md-6">
                            <Form.Label className="fw-bold">Права редактирования</Form.Label>
                            <Form.Select value={editPolicy} onChange={(e) => setEditPolicy(e.target.value)}>
                                {Object.entries(WIKI_PAGE_EDIT_POLICY_LABELS).map(([key, label]) => (
                                    <option key={key} value={key}>{label}</option>
                                ))}
                            </Form.Select>
                            <Form.Text className="text-muted">
                                OPEN — редактируют все авторизованные. OWNER_ONLY — только автор.
                            </Form.Text>
                        </Form.Group>
                    </div>

                    {/* 3. Блок с тегами */}
                    <Form.Group className="mb-4 tags-selector">
                        <Form.Label className="fw-bold mb-3 d-block">Категории (Теги)</Form.Label>
                        <div className="d-flex flex-wrap gap-3">
                            {tags.map((t) => (
                                <Form.Check
                                    key={t.id}
                                    type="checkbox"
                                    id={`tag-${t.id}`}
                                    label={t.name}
                                    checked={selectedTagIds.includes(t.id)}
                                    onChange={(e) => {
                                        if (e.target.checked) setSelectedTagIds([...selectedTagIds, t.id]);
                                        else setSelectedTagIds(selectedTagIds.filter(tid => tid !== t.id));
                                    }}
                                />
                            ))}
                            {tags.length === 0 && <span className="text-muted">Тегов пока нет.</span>}
                        </div>
                    </Form.Group>

                    {/* 4. Кнопки (sticky) */}
                    <div className="sticky-save-bar">
                        <Button type="submit" disabled={saving} className="flex-grow-1" size="lg">
                            {saving ? "Сохранение..." : "Сохранить статью"}
                        </Button>
                        <Button variant="outline-secondary" onClick={() => navigate(-1)} size="lg">
                            Отмена
                        </Button>
                    </div>
                </Form>
            </div>
        </div>
    );
};