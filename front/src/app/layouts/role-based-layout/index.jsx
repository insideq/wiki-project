import { ForbiddenPage } from "@pages/forbidden-page";
import { useAuth } from "@shared/auth";
import { Navigate, Outlet } from "react-router-dom";

export const RoleBasedLayout = ({ requiredRoles = [] }) => {
    const { user, isAuthenticated } = useAuth();

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (requiredRoles.length > 0) {
        const roles = requiredRoles.map((role) => `ROLE_${role.toUpperCase()}`);
        const hasRequiredRole = roles.includes(user?.details?.role);

        if (!hasRequiredRole) {
            return <ForbiddenPage />;
        }
    }

    return <Outlet />;
};