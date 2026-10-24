import { useModal } from "@shared/modal";
import { Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context";

export const UserButton = ({ className }) => {
    const { user, logout, isAuthenticated } = useAuth();
    const { show } = useModal();
    const navigate = useNavigate();

    if (!isAuthenticated) {
        return null;
    }

    const userName = user?.details?.login;

    const handleClick = () => {
        show("Выход", "Завершить работу?", async () => {
            await logout();
            navigate("/");
            window.location.reload();
        });
    };

    return (
        <Button variant="primary" onClick={handleClick} className={className}>
            Выход {userName ? `(${userName})` : ""}
        </Button>
    );
};