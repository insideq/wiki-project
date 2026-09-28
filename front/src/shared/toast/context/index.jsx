import { lsReadArray, lsSave } from "@shared/storage";
import { createContext, useContext, useEffect, useReducer } from "react";

const KEY = "toasts";

const ToastContext = createContext(null);
const ToastDispatcherContext = createContext(null);

// eslint-disable-next-line react-refresh/only-export-components
export const TOAST_ACTION = {
    add: "add",
    hide: "hide",
    delete: "delete",
    deleteAll: "delete-all",
};

const getDate = () => {
    const zeroPad = (num) => String(num).padStart(2, "0");
    const date = new Date();
    const day = zeroPad(date.getDate());
    const month = zeroPad(date.getMonth() + 1);
    const hours = zeroPad(date.getHours());
    const minutes = zeroPad(date.getMinutes());
    return `${day}.${month} ${hours}:${minutes}`;
};

const toastsReducer = (toasts, action) => {
    switch (action.type) {
        case TOAST_ACTION.add: {
            return [
                ...toasts,
                {
                    id: crypto.randomUUID(),
                    title: action.title,
                    text: action.text,
                    date: getDate(),
                    show: true,
                },
            ];
        }
        case TOAST_ACTION.hide: {
            return toasts.map((toast) => {
                if (toast.id === action.id) {
                    return { ...toast, show: false };
                } else {
                    return toast;
                }
            });
        }
        case TOAST_ACTION.delete: {
            return toasts.filter((toast) => toast.id !== action.id);
        }
        case TOAST_ACTION.deleteAll: {
            return [];
        }
        default: {
            throw Error("Unknown action: " + action.type);
        }
    }
};

export const ToastProvider = ({ children }) => {
    const [toasts, dispatch] = useReducer(toastsReducer, [], () => lsReadArray(KEY));

    useEffect(() => {
        lsSave(KEY, toasts);
    }, [toasts]);

    return (
        <ToastContext.Provider value={toasts}>
            <ToastDispatcherContext.Provider value={dispatch}>{children}</ToastDispatcherContext.Provider>
        </ToastContext.Provider>
    );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useToasts = () => {
    return useContext(ToastContext);
};

// eslint-disable-next-line react-refresh/only-export-components
export const useToastsDispatch = () => {
    return useContext(ToastDispatcherContext);
};
