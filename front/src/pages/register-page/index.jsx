import { useAuth } from "@shared/auth";
import { apiCreateItem } from "@shared/client";
import { useBSForm } from "@shared/form";
import { useState } from "react";
import { Alert, Button, Container, Form } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import "./styles.css";

export const RegisterPage = () => {
    const { login } = useAuth();
    const navigate = useNavigate();
    const { register, validated, handleSubmit } = useBSForm(null, false);
    const [isSubmit, setIsSubmit] = useState(false);
    const [error, setError] = useState("");

    const handleRegister = async (data) => {
        setIsSubmit(true);
        setError("");
        try {
            // Создаём пользователя
            await apiCreateItem("auth/signup", data);
            // Сразу логиним его
            await login({ login: data.login, password: data.password });
            navigate("/wiki");
        } catch (e) {
            setError(e.message);
        } finally {
            setIsSubmit(false);
        }
    };

    return (
        <main className="flex-grow-1 d-flex align-items-center bg-light">
            <Container style={{ maxWidth: "400px" }}>
                <div className="bg-white p-4 rounded shadow-sm">
                    <h3 className="text-center mb-4">Регистрация</h3>
                    {error && <Alert variant="danger">{error}</Alert>}
                    <Form noValidate validated={validated} onSubmit={(e) => handleSubmit(e, handleRegister)}>
                        <Form.Group className="mb-3">
                            <Form.Label>Логин</Form.Label>
                            <Form.Control type="text" required minLength={3} {...register("login")} />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Пароль</Form.Label>
                            <Form.Control type="password" required minLength={3} {...register("password")} />
                        </Form.Group>
                        <Form.Group className="mb-4">
                            <Form.Label>Повторите пароль</Form.Label>
                            <Form.Control type="password" required minLength={3} {...register("passwordConfirm")} />
                        </Form.Group>
                        <Button type="submit" className="w-100 mb-3" disabled={isSubmit}>
                            {isSubmit ? "Регистрация..." : "Зарегистрироваться"}
                        </Button>
                        <div className="text-center small">
                            Уже есть аккаунт? <Link to="/login">Войти</Link>
                        </div>
                    </Form>
                </div>
            </Container>
        </main>
    );
};