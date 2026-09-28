import { createContext, useCallback, useContext, useMemo, useState } from "react";

const ModalContext = createContext(null);

export const ModalProvider = ({ children }) => {
    const [modal, setModal] = useState(null);

    const show = useCallback((title, text, onConfirm) => {
        const close = () => setModal(() => null);
        const newModal = {};
        newModal.id = crypto.randomUUID();
        newModal.title = title;
        newModal.text = text;
        newModal.onConfirm = async () => {
            await onConfirm();
            close();
        };
        newModal.onClose = () => close();
        setModal(() => newModal);
    }, []);

    const contextValue = useMemo(
        () => ({
            modal,
            show,
        }),
        [modal, show]
    );

    return <ModalContext.Provider value={contextValue}>{children}</ModalContext.Provider>;
};

// eslint-disable-next-line react-refresh/only-export-components
export const useModal = () => {
    return useContext(ModalContext);
};
