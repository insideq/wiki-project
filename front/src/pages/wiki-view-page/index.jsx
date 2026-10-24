import { createDeleteRequest } from "@entities/delete-request/api";
import { useWikiMutations } from "@entities/wiki-page/hooks/use-wiki-mutations";
import { useWikiPage } from "@entities/wiki-page/hooks/use-wiki-page";
import { useAuth } from "@shared/auth";
import { useModal } from "@shared/modal";
import { TOAST_ACTION, useToastsDispatch } from "@shared/toast/context";
import { MdViewer } from "@widgets/md-viewer";
import { useState } from "react";
import { Alert, Button, Spinner } from "react-bootstrap";
import { Link, useNavigate, useParams } from "react-router-dom";

export const WikiViewPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { data, loading, error } = useWikiPage(id);
    const { remove } = useWikiMutations();
    const { user } = useAuth();
    const { show } = useModal();
    const dispatch = useToastsDispatch();
    const [isActionLoading, setIsActionLoading] = useState(false);

    if (loading) return <div className="text-center mt-5"><Spinner animation="border" /></div>;
    if (error) return <div className="alert alert-danger mt-3">{error}</div>;
    if (!data) return null;

    const isAdmin = user?.details?.role === "ROLE_ADMIN";
    const isAuthor = user?.details?.login === data.authorLogin;
    const canEdit = isAdmin || (isAuthor && data.status === "ACTIVE");
    const isPendingDeletion = data.status === "PENDING_DELETION";

    // Удаление админом (сразу)
    const handleAdminDelete = () => {
        show("Удаление", `Удалить статью "${data.title}" безвозвратно?`, async () => {
            try {
                setIsActionLoading(true);
                await remove(id);
                dispatch({ type: TOAST_ACTION.add, title: "Успех", text: "Статья удалена" });
                navigate("/wiki");
            } catch (e) {
                dispatch({ type: TOAST_ACTION.add, title: "Ошибка", text: e.message });
            } finally {
                setIsActionLoading(false);
            }
        });
    };

    // Запрос на удаление (от автора)
    const handleRequestDelete = () => {
        show("Запрос на удаление", "Отправить запрос администратору на удаление этой статьи?", async () => {
            try {
                setIsActionLoading(true);
                await createDeleteRequest(id);
                dispatch({ type: TOAST_ACTION.add, title: "Успех", text: "Запрос отправлен" });
                window.location.reload(); // перезагружаем, чтобы увидеть статус
            } catch (e) {
                dispatch({ type: TOAST_ACTION.add, title: "Ошибка", text: e.message });
            } finally {
                setIsActionLoading(false);
            }
        });
    };

    return (
        <div className="container mt-3">
            <h1>{data.title}</h1>
            
            {isPendingDeletion && (
                <Alert variant="warning">
                    Эта статья ожидает удаления. Администратор рассмотрит запрос.
                </Alert>
            )}

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
                {canEdit && !isPendingDeletion && (
                    <Button as={Link} to={`/wiki/${data.id}/edit`} variant="outline-primary" size="sm">
                        Редактировать
                    </Button>
                )}
                
                {isAdmin && (
                    <Button variant="danger" size="sm" onClick={handleAdminDelete} disabled={isActionLoading}>
                        Удалить (Админ)
                    </Button>
                )}
                
                {!isAdmin && isAuthor && !isPendingDeletion && (
                    <Button variant="outline-danger" size="sm" onClick={handleRequestDelete} disabled={isActionLoading}>
                        Запросить удаление
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