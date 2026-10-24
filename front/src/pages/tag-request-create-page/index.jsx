import { createTagRequest } from "@entities/tag-request/api";
import { TOAST_ACTION, useToastsDispatch } from "@shared/toast/context";
import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

export const TagRequestCreatePage = () => {
    const [name, setName] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const navigate = useNavigate();
    const dispatch = useToastsDispatch();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        try {
            await createTagRequest({ name });
            dispatch({ 
                type: TOAST_ACTION.add, 
                title: "Успех", 
                text: "Запрос на тег отправлен на модерацию" 
            });
            navigate("/wiki");
        } catch (e) {
            dispatch({ type: TOAST_ACTION.add, title: "Ошибка", text: e.message });
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="container mt-3" style={{ maxWidth: "500px" }}>
            <h2>Предложить тег</h2>
            <p className="text-muted">Запрос будет отправлен администратору на проверку.</p>
            
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Название тега</Form.Label>
                    <Form.Control
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                        maxLength={50}
                        placeholder="Например: Java"
                    />
                </Form.Group>
                <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? "Отправка..." : "Отправить"}
                </Button>
            </Form>
        </div>
    );
};