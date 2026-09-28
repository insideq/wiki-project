import { useWikiMutations } from "@entities/wiki-page/hooks/use-wiki-mutations";
import { useWikiPage } from "@entities/wiki-page/hooks/use-wiki-page";
import { useAuth } from "@shared/auth";
import { useModal } from "@shared/modal";
import { MdViewer } from "@widgets/md-viewer";
import { Button, Spinner } from "react-bootstrap";
import { Link, useNavigate, useParams } from "react-router-dom";

export const WikiViewPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { data, loading, error } = useWikiPage(id);
    const { remove } = useWikiMutations();
    const { user } = useAuth();
    const { show } = useModal();

    if (loading) return <Spinner animation="border" />;
    if (error) return <div className="text-danger">{error}</div>;
    if (!data) return null;

    const canEdit =
        user?.details?.role === "ROLE_ADMIN" || user?.details?.login === data.authorLogin;

    const handleDelete = () => {
        show("Удаление", `Удалить статью "${data.title}"?`, async () => {
            await remove(id);
            navigate("/wiki");
        });
    };

    return (
        <div className="container mt-3">
            <h1>{data.title}</h1>
            <div className="text-muted mb-2">
                Автор: {data.authorLogin} · {new Date(data.createdAt).toLocaleString()}
            </div>
            <div className="mb-3">
                {data.tags?.map((tag) => (
                    <span key={tag.id} className="badge bg-secondary me-1">
                        {tag.name}
                    </span>
                ))}
            </div>

            <div className="d-flex gap-2 mb-3">
                {canEdit && (
                    <Button as={Link} to={`/wiki/${data.id}/edit`} variant="outline-primary" size="sm">
                        Редактировать
                    </Button>
                )}
                {user?.details?.role === "ROLE_ADMIN" && (
                    <Button variant="outline-danger" size="sm" onClick={handleDelete}>
                        Удалить
                    </Button>
                )}
                <Button as={Link} to="/wiki" variant="outline-secondary" size="sm">
                    Назад
                </Button>
            </div>

            <MdViewer content={data.content} />
        </div>
    );
};