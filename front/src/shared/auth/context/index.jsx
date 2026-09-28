import {
    apiLogin,
    apiLogout,
    apiWhoAmI,
    getAuthToken,
    getCurrentUser,
    setAuthToken,
    setCurrentUser,
} from "@shared/index";
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { authObserver } from "../observer";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    const isAuthenticated = !!user;

    const updateUser = (token, details) => {
        setAuthToken(token);
        setCurrentUser(details);
        setUser({ token, details });
    };

    const clearCredentials = () => {
        setAuthToken(null);
        setCurrentUser(null);
        setUser(null);
    };

    const login = useCallback(async (credentials) => {
        try {
            const token = await apiLogin(credentials);
            setAuthToken(token);
            const user = await apiWhoAmI();
            updateUser(token, user);
        } catch (e) {
            clearCredentials();
            throw e;
        }
    }, []);

    const logout = useCallback(async () => {
        try {
            await apiLogout();
        } catch (e) {
            console.log(e);
        } finally {
            clearCredentials();
        }
    }, []);

    useEffect(() => {
        const handleStorageChange = (event) => {
            if (event.storageArea === localStorage) {
                switch (event.key) {
                    case "authToken":
                    case "currentUser":
                        logout();
                        break;
                    default:
                        break;
                }
            }
        };

        window.addEventListener("storage", handleStorageChange);
        return () => window.removeEventListener("storage", handleStorageChange);
    }, [logout]);

    useEffect(() => {
        // Проверяем аутентификацию при загрузке приложения
        const updateAuth = async () => {
            try {
                const authToken = getAuthToken();
                const currentUser = getCurrentUser();
                if (!!authToken && !!currentUser) {
                    const user = await apiWhoAmI();
                    updateUser(getAuthToken(), user);
                }
            } catch (e) {
                console.log(e);
                clearCredentials();
            }
        };

        updateAuth();
    }, []);

    useEffect(() => {
        const unsubscribe = authObserver.subscribe(() => {
            clearCredentials();
        });

        return unsubscribe;
    }, []);

    const contextValue = useMemo(
        () => ({
            user,
            login,
            logout,
            isAuthenticated,
        }),
        [user, login, logout, isAuthenticated]
    );

    return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
    return useContext(AuthContext);
};
