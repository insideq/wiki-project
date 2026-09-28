import { useAuth } from "@shared/auth";
import { useBSForm } from "@shared/form";
import { useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import { JournalText } from "react-bootstrap-icons";
import "./styles.css";

export const LoginPage = () => {
    const { login } = useAuth();

    const { register, validated, handleSubmit, resetField, setFocus } = useBSForm(null, false);
    const [isSubmit, setIsSubmit] = useState(false);
    const [credentials, setCredentials] = useState({ login: "", password: "" });
    const [error, setError] = useState("");

    setFocus("login");

    const handleLogin = async (data) => {
        setIsSubmit(true);
        try {
            await login(data);
        } catch (error) {
            setError(error.message);
            setCredentials({ ...credentials, password: "" });
            resetField("password");
            setTimeout(() => setFocus("password"), 5);
        } finally {
            setIsSubmit(false);
        }
    };

    return (
        <main className="flex-grow-1 container-fluid p-0">
            <div className="row justify-content-center align-items-md-center login-form-container">
                <div className="col-md-6 col-lg-4 col p-2 p-md-3 login-form">
                    <div className="text-center fs-3">
                        <JournalText className="me-1" />
                        Wiki
                    </div>
                    {error && (
                        <Alert variant="danger">
                            <div>Ошибка входа в систему</div>
                            <div className="small">{error}</div>
                        </Alert>
                    )}
                    <Form noValidate validated={validated} onSubmit={(event) => handleSubmit(event, handleLogin)}>
                        <Form.Group className="mb-2" controlId="login">
                            <Form.Label>Имя пользователя</Form.Label>
                            <Form.Control
                                type="text"
                                minLength={3}
                                required
                                disabled={isSubmit}
                                autoComplete="username"
                                {...register("login")}
                            />
                        </Form.Group>
                        <Form.Group className="mb-2" controlId="password">
                            <Form.Label>Пароль</Form.Label>
                            <Form.Control
                                type="password"
                                minLength={3}
                                required
                                disabled={isSubmit}
                                autoComplete="current-password"
                                {...register("password")}
                            />
                        </Form.Group>
                        <div className="text-center">
                            <Button type="submit" disabled={isSubmit} className="px-5">
                                Войти
                            </Button>
                        </div>
                    </Form>
                </div>
            </div>
        </main>
    );
};