import { ToastContainer as BSToastContainer, Toast } from "react-bootstrap";
import { TOAST_ACTION, useToasts, useToastsDispatch } from "../../context";

export const ToastContainer = () => {
    const toasts = useToasts();
    const toast = useToastsDispatch();

    const handleClose = (id) => {
        toast({
            type: TOAST_ACTION.hide,
            id,
        });
    };

    const content = toasts
        .filter((toast) => toast.show)
        .map((toast) => {
            return (
                <Toast key={toast.id} onClose={() => handleClose(toast.id)} bg="light" delay={3000} autohide>
                    <Toast.Header>
                        <strong className="me-auto">{toast.title}</strong>
                        <small className="text-muted">{toast.date}</small>
                    </Toast.Header>
                    <Toast.Body>{toast.text}</Toast.Body>
                </Toast>
            );
        });

    return (
        <BSToastContainer position="top-end" className="p-2 pt-5">
            {content}
        </BSToastContainer>
    );
};
