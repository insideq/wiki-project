import { useTagRequests } from "@entities/tag-request";
import { approveTagRequest, rejectTagRequest } from "@entities/tag-request/api";
import { TOAST_ACTION, useToastsDispatch } from "@shared/toast/context";
import { useState } from "react";
import { Button, Spinner, Table } from "react-bootstrap";

export const TagRequestsPage = () => {
    const { data, loading, error, reload } = useTagRequests();
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
            <h2>Запросы на создание тегов</h2>
            {loading && <Spinner animation="border" />}
            {error && <div className="text-danger">{error}</div>}
            
            {data?.length === 0 && !loading && <p>Нет активных запросов.</p>}

            {data?.length > 0 && (
                <Table striped bordered hover responsive>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Название тега</th>
                            <th>Запросил</th>
                            <th>Дата</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map((req) => (
                            <tr key={req.id}>
                                <td>{req.id}</td>
                                <td>{req.name}</td>
                                <td>{req.requestedBy}</td>
                                <td>{new Date(req.createdAt).toLocaleString()}</td>
                                <td>
                                    <Button 
                                        variant="success" 
                                        size="sm" 
                                        className="me-2"
                                        disabled={actionId === req.id}
                                        onClick={() => handleAction(req.id, approveTagRequest)}
                                    >
                                        Одобрить
                                    </Button>
                                    <Button 
                                        variant="danger" 
                                        size="sm"
                                        disabled={actionId === req.id}
                                        onClick={() => handleAction(req.id, rejectTagRequest)}
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