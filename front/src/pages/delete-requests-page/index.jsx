import { useDeleteRequests } from "@entities/delete-request";
import { approveDeleteRequest, rejectDeleteRequest } from "@entities/delete-request/api";
import { TOAST_ACTION, useToastsDispatch } from "@shared/toast/context";
import { useState } from "react";
import { Button, Spinner, Table } from "react-bootstrap";

export const DeleteRequestsPage = () => {
    const { data, loading, error, reload } = useDeleteRequests();
    const dispatch = useToastsDispatch();
    const [actionId, setActionId] = useState(null);

    const handleAction = async (id, actionFn) => {
        setActionId(id);
        try {
            await actionFn(id);
            dispatch({ type: TOAST_ACTION.add, title: "Успех", text: "Запрос обработан" });
            reload();
        } catch (e) {
            dispatch({ type: TOAST_ACTION.add, title: "Ошибка", text: e.message });
        } finally {
            setActionId(null);
        }
    };

    return (
        <div className="container mt-3">
            <h2>Запросы на удаление статей</h2>
            {loading && <Spinner animation="border" />}
            {error && <div className="text-danger">{error}</div>}
            
            {data?.length === 0 && !loading && <p>Нет активных запросов.</p>}

            {data?.length > 0 && (
                <Table striped bordered hover responsive>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Статья</th>
                            <th>Запросил</th>
                            <th>Дата</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map((req) => (
                            <tr key={req.id}>
                                <td>{req.id}</td>
                                <td>{req.pageTitle}</td>
                                <td>{req.requestedBy}</td>
                                <td>{new Date(req.createdAt).toLocaleString()}</td>
                                <td>
                                    <Button 
                                        variant="danger" 
                                        size="sm" 
                                        className="me-2"
                                        disabled={actionId === req.id}
                                        onClick={() => handleAction(req.id, approveDeleteRequest)}
                                    >
                                        Удалить
                                    </Button>
                                    <Button 
                                        variant="secondary" 
                                        size="sm"
                                        disabled={actionId === req.id}
                                        onClick={() => handleAction(req.id, rejectDeleteRequest)}
                                    >
                                        Отклонить
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}
        </div>
    );
};