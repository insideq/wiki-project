import { LoginPage } from "@pages/login-page";
import { useAuth } from "@shared/auth";
import { Outlet } from "react-router-dom";

export const AuthLayout = () => {
    const { isAuthenticated } = useAuth();

    if (isAuthenticated) {
        return <Outlet />;
    }

    return <LoginPage />;
};
