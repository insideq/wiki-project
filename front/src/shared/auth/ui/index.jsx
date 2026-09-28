import { useModal } from "@shared/modal";
import { Button } from "react-bootstrap";
import { useAuth } from "../context";

export const UserButton = ({ className }) => {
    const { user, logout, isAuthenticated } = useAuth();
    const { show } = useModal();

    if (!isAuthenticated) {
        return null;
    }

    const userName = user?.details?.login;

    const handleClick = () => {
        show("Выход", "Завершить работу?", async () => await logout());
    };

    return (
        <Button variant="primary" onClick={handleClick} className={className}>
            Выход {userName ? `(${userName})` : ""}
        </Button>
    );
};
