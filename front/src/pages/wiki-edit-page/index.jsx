import { useTags } from "@entities/tag/hooks/use-tags";
import { useWikiMutations } from "@entities/wiki-page/hooks/use-wiki-mutations";
import { useWikiPage } from "@entities/wiki-page/hooks/use-wiki-page";
import {
    WIKI_PAGE_EDIT_POLICY,
    WIKI_PAGE_EDIT_POLICY_LABELS,
    WIKI_PAGE_VISIBILITY,
    WIKI_PAGE_VISIBILITY_LABELS,
} from "@entities/wiki-page/model/enums";
import { MdEditor } from "@widgets/md-editor";
import { useEffect, useState } from "react";
import { Button, Form, Spinner } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

export const WikiEditPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;
    const { create, update } = useWikiMutations();
    const { data: existing, loading: loadingExisting } = useWikiPage(id);
    const { data: tags } = useTags();

    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [visibility, setVisibility] = useState(WIKI_PAGE_VISIBILITY.PUBLIC);
    const [editPolicy, setEditPolicy] = useState(WIKI_PAGE_EDIT_POLICY.OPEN);
    const [selectedTagIds, setSelectedTagIds] = useState([]);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (existing) {
            setTitle(existing.title || "");
            setContent(existing.content || "");
            setVisibility(existing.visibility || WIKI_PAGE_VISIBILITY.PUBLIC);
            setEditPolicy(existing.editPolicy || WIKI_PAGE_EDIT_POLICY.OPEN);
            setSelectedTagIds(existing.tags?.map((t) => t.id) || []);
        }
    }, [existing]);

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

    if (isEdit && loadingExisting) return <Spinner animation="border" />;

    return (
        <div className="container mt-3">
            <h2>{isEdit ? "Редактирование статьи" : "Создание статьи"}</h2>

            {error && <div className="alert alert-danger">{error}</div>}

            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Заголовок</Form.Label>
                    <Form.Control
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                        maxLength={200}
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Содержимое (Markdown)</Form.Label>
                    <MdEditor value={content} onChange={setContent} />
                </Form.Group>

                <div className="row">
                    <Form.Group className="mb-3 col-md-6">
                        <Form.Label>Видимость</Form.Label>
                        <Form.Select value={visibility} onChange={(e) => setVisibility(e.target.value)}>
                            {Object.entries(WIKI_PAGE_VISIBILITY_LABELS).map(([key, label]) => (
                                <option key={key} value={key}>
                                    {label}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>

                    <Form.Group className="mb-3 col-md-6">
                        <Form.Label>Права редактирования</Form.Label>
                        <Form.Select value={editPolicy} onChange={(e) => setEditPolicy(e.target.value)}>
                            {Object.entries(WIKI_PAGE_EDIT_POLICY_LABELS).map(([key, label]) => (
                                <option key={key} value={key}>
                                    {label}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>
                </div>

                <Form.Group className="mb-3">
                    <Form.Label>Теги</Form.Label>
                    <Form.Control
                        as="select"
                        multiple
                        value={selectedTagIds.map(String)}
                        onChange={(e) =>
                            setSelectedTagIds(Array.from(e.target.selectedOptions, (o) => Number(o.value)))
                        }
                    >
                        {tags.map((t) => (
                            <option key={t.id} value={t.id}>
                                {t.name}
                            </option>
                        ))}
                    </Form.Control>
                </Form.Group>

                <div className="d-flex gap-2">
                    <Button type="submit" disabled={saving}>
                        {saving ? "Сохранение..." : "Сохранить"}
                    </Button>
                    <Button variant="secondary" onClick={() => navigate(-1)}>
                        Отмена
                    </Button>
                </div>
            </Form>
        </div>
    );
};